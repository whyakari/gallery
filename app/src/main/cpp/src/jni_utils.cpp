#include "native_bridge.h"
#include <jni.h>
#include <android/bitmap.h>
#include <vector>
#include <string>
#include <cmath>

namespace jni_utils {

bool bitmapToPixels(JNIEnv* env, jobject bitmap, AndroidBitmapInfo* info, void** pixels) {
    if (AndroidBitmap_getInfo(env, bitmap, info) < 0) {
        return false;
    }

    if (info->format != ANDROID_BITMAP_FORMAT_RGBA_8888 &&
        info->format != ANDROID_BITMAP_FORMAT_RGB_565) {
        return false;
    }

    if (AndroidBitmap_lockPixels(env, bitmap, pixels) < 0) {
        return false;
    }

    return true;
}

jobject createBitmap(JNIEnv* env, int width, int height, const void* pixels, int format) {
    jclass bitmapClass = env->FindClass("android/graphics/Bitmap");
    if (!bitmapClass) return nullptr;

    jmethodID createBitmapMethod = env->GetStaticMethodID(
        bitmapClass, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    if (!createBitmapMethod) return nullptr;

    jclass configClass = env->FindClass("android/graphics/Bitmap$Config");
    if (!configClass) return nullptr;

    jfieldID argb8888Field = env->GetStaticFieldID(configClass, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    if (!argb8888Field) return nullptr;

    jobject config = env->GetStaticObjectField(configClass, argb8888Field);
    if (!config) return nullptr;

    jobject bitmap = env->CallStaticObjectMethod(bitmapClass, createBitmapMethod, width, height, config);
    if (!bitmap) return nullptr;

    // Copy pixels to bitmap
    AndroidBitmapInfo info;
    void* bitmapPixels;
    if (AndroidBitmap_getInfo(env, bitmap, &info) >= 0 &&
        AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels) >= 0) {
        int stride = info.stride;
        int pixelSize = (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) ? 4 : 2;
        for (int y = 0; y < height; ++y) {
            memcpy(
                static_cast<char*>(bitmapPixels) + y * stride,
                static_cast<const char*>(pixels) + y * width * pixelSize,
                width * pixelSize
            );
        }
        AndroidBitmap_unlockPixels(env, bitmap);
    }

    return bitmap;
}

std::vector<float> floatArrayToVector(JNIEnv* env, jfloatArray array) {
    if (!array) return {};
    jsize len = env->GetArrayLength(array);
    std::vector<float> vec(len);
    env->GetFloatArrayRegion(array, 0, len, vec.data());
    return vec;
}

jfloatArray vectorToFloatArray(JNIEnv* env, const std::vector<float>& vec) {
    jfloatArray array = env->NewFloatArray(vec.size());
    if (array) {
        env->SetFloatArrayRegion(array, 0, vec.size(), vec.data());
    }
    return array;
}

std::vector<int> intArrayToVector(JNIEnv* env, jintArray array) {
    if (!array) return {};
    jsize len = env->GetArrayLength(array);
    std::vector<int> vec(len);
    env->GetIntArrayRegion(array, 0, len, vec.data());
    return vec;
}

jintArray vectorToIntArray(JNIEnv* env, const std::vector<int>& vec) {
    jintArray array = env->NewIntArray(vec.size());
    if (array) {
        env->SetIntArrayRegion(array, 0, vec.size(), vec.data());
    }
    return array;
}

jobject createRect(JNIEnv* env, int left, int top, int right, int bottom) {
    jclass rectClass = env->FindClass("android/graphics/Rect");
    if (!rectClass) return nullptr;

    jmethodID constructor = env->GetMethodID(rectClass, "<init>", "(IIII)V");
    if (!constructor) return nullptr;

    return env->NewObject(rectClass, constructor, left, top, right, bottom);
}

jobject createClassificationResult(JNIEnv* env, const ClassificationResult& result) {
    jclass clazz = env->FindClass("com/example/gallery/native/NativeBridge$ClassificationResult");
    if (!clazz) return nullptr;

    jmethodID constructor = env->GetMethodID(clazz, "<init>", "(Ljava/lang/String;FLjava/lang/String;)V");
    if (!constructor) return nullptr;

    jstring label = env->NewStringUTF(result.label.c_str());
    jstring category = env->NewStringUTF(result.category.c_str());

    return env->NewObject(clazz, constructor, label, result.confidence, category);
}

jobject createFaceDetectionResult(JNIEnv* env, const FaceDetectionResult& result) {
    jclass clazz = env->FindClass("com/example/gallery/native/NativeBridge$FaceDetectionResult");
    if (!clazz) return nullptr;

    jmethodID constructor = env->GetMethodID(
        clazz, "<init>", "(Landroid/graphics/Rect;F[FFFFF)V");
    if (!constructor) return nullptr;

    jobject rect = createRect(env, result.boundingBox.left, result.boundingBox.top,
                              result.boundingBox.right, result.boundingBox.bottom);
    jfloatArray landmarks = vectorToFloatArray(env,
        std::vector<float>(result.landmarks, result.landmarks + 8));

    return env->NewObject(clazz, constructor, rect, result.confidence, landmarks,
                          result.yaw, result.pitch, result.roll);
}

jobject createSceneDetectionResult(JNIEnv* env, const SceneDetectionResult& result) {
    jclass clazz = env->FindClass("com/example/gallery/native/NativeBridge$SceneDetectionResult");
    if (!clazz) return nullptr;

    jmethodID constructor = env->GetMethodID(
        clazz, "<init>", "(Ljava/lang/String;FLjava/util/Map;)V");
    if (!constructor) return nullptr;

    jstring sceneType = env->NewStringUTF(result.sceneType.c_str());

    // Create HashMap for attributes
    jclass mapClass = env->FindClass("java/util/HashMap");
    jmethodID mapConstructor = env->GetMethodID(mapClass, "<init>", "()V");
    jobject map = env->NewObject(mapClass, mapConstructor);
    jmethodID putMethod = env->GetMethodID(mapClass, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

    for (const auto& [key, value] : result.attributes) {
        jstring k = env->NewStringUTF(key.c_str());
        jobject v = env->CallStaticObjectMethod(
            env->FindClass("java/lang/Float"),
            env->GetStaticMethodID(env->FindClass("java/lang/Float"), "valueOf", "(F)Ljava/lang/Float;"),
            value
        );
        env->CallObjectMethod(map, putMethod, k, v);
    }

    return env->NewObject(clazz, constructor, sceneType, result.confidence, map);
}

jobject createObjectDetectionResult(JNIEnv* env, const ObjectDetectionResult& result) {
    jclass clazz = env->FindClass("com/example/gallery/native/NativeBridge$ObjectDetectionResult");
    if (!clazz) return nullptr;

    jmethodID constructor = env->GetMethodID(
        clazz, "<init>", "(Landroid/graphics/Rect;Ljava/lang/String;FLjava/lang/String;)V");
    if (!constructor) return nullptr;

    jobject rect = createRect(env, result.boundingBox.left, result.boundingBox.top,
                              result.boundingBox.right, result.boundingBox.bottom);
    jstring label = env->NewStringUTF(result.label.c_str());
    jstring category = env->NewStringUTF(result.category.c_str());

    return env->NewObject(clazz, constructor, rect, label, result.confidence, category);
}

jobject createTextDetectionResult(JNIEnv* env, const TextDetectionResult& result) {
    jclass clazz = env->FindClass("com/example/gallery/native/NativeBridge$TextDetectionResult");
    if (!clazz) return nullptr;

    jmethodID constructor = env->GetMethodID(
        clazz, "<init>", "(Landroid/graphics/Rect;Ljava/lang/String;FLjava/lang/String;)V");
    if (!constructor) return nullptr;

    jobject rect = createRect(env, result.boundingBox.left, result.boundingBox.top,
                              result.boundingBox.right, result.boundingBox.bottom);
    jstring text = env->NewStringUTF(result.text.c_str());
    jstring language = result.language ? env->NewStringUTF(result.language.c_str()) : nullptr;

    return env->NewObject(clazz, constructor, rect, text, result.confidence, language);
}

void throwException(JNIEnv* env, const char* className, const char* message) {
    jclass clazz = env->FindClass(className);
    if (clazz) {
        env->ThrowNew(clazz, message);
    }
}

} // namespace jni_utils