#ifndef GALLERY_NATIVE_BRIDGE_H
#define GALLERY_NATIVE_BRIDGE_H

#include <jni.h>
#include <android/bitmap.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include <vector>
#include <string>
#include <memory>

// Forward declarations
struct FaceDetectionResult;
struct ClassificationResult;
struct SceneDetectionResult;
struct ObjectDetectionResult;
struct TextDetectionResult;

// Opaque handles for models
struct FaceDetector;
struct FaceEmbedder;
struct ImageClassifier;
struct BlurDetector;
struct AestheticScorer;
struct ColorExtractor;
struct AutoEnhancer;
struct VideoAnalyzer;
struct SceneDetector;
struct ObjectDetector;
struct TextDetector;

// Global context
struct NativeContext {
    AAssetManager* assetManager = nullptr;
    std::unique_ptr<FaceDetector> faceDetector;
    std::unique_ptr<FaceEmbedder> faceEmbedder;
    std::unique_ptr<ImageClassifier> imageClassifier;
    std::unique_ptr<BlurDetector> blurDetector;
    std::unique_ptr<AestheticScorer> aestheticScorer;
    std::unique_ptr<ColorExtractor> colorExtractor;
    std::unique_ptr<AutoEnhancer> autoEnhancer;
    std::unique_ptr<VideoAnalyzer> videoAnalyzer;
    std::unique_ptr<SceneDetector> sceneDetector;
    std::unique_ptr<ObjectDetector> objectDetector;
    std::unique_ptr<TextDetector> textDetector;
};

extern NativeContext g_nativeContext;

// JNI function declarations
extern "C" {

// Initialization
JNIEXPORT void JNICALL
Java_com_example_gallery_native_NativeBridge_nativeInit(
    JNIEnv* env, jclass clazz, jobject assetManager);

JNIEXPORT void JNICALL
Java_com_example_gallery_native_NativeBridge_nativeRelease(
    JNIEnv* env, jclass clazz);

// Face Detection
JNIEXPORT jobjectArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeDetectFaces(
    JNIEnv* env, jclass clazz, jobject bitmap, jfloat minFaceSize);

// Face Alignment
JNIEXPORT jobject JNICALL
Java_com_example_gallery_native_NativeBridge_nativeAlignFace(
    JNIEnv* env, jclass clazz, jobject bitmap, jfloatArray landmarks);

// Face Embedding
JNIEXPORT jfloatArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeGetFaceEmbedding(
    JNIEnv* env, jclass clazz, jobject alignedFace);

// Face Comparison
JNIEXPORT jfloat JNICALL
Java_com_example_gallery_native_NativeBridge_nativeCompareFaces(
    JNIEnv* env, jclass clazz, jfloatArray embedding1, jfloatArray embedding2);

// Image Classification
JNIEXPORT jobjectArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeClassifyImage(
    JNIEnv* env, jclass clazz, jobject bitmap, jint maxResults);

// Blur Detection
JNIEXPORT jfloat JNICALL
Java_com_example_gallery_native_NativeBridge_nativeDetectBlur(
    JNIEnv* env, jclass clazz, jobject bitmap);

// Aesthetic Scoring
JNIEXPORT jfloat JNICALL
Java_com_example_gallery_native_NativeBridge_nativeScoreAesthetic(
    JNIEnv* env, jclass clazz, jobject bitmap);

// Dominant Color Extraction
JNIEXPORT jintArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeExtractDominantColors(
    JNIEnv* env, jclass clazz, jobject bitmap, jint colorCount);

// Auto Enhance
JNIEXPORT jobject JNICALL
Java_com_example_gallery_native_NativeBridge_nativeAutoEnhance(
    JNIEnv* env, jclass clazz, jobject bitmap);

// Video Stabilization Analysis
JNIEXPORT jfloat JNICALL
Java_com_example_gallery_native_NativeBridge_nativeAnalyzeVideoStabilization(
    JNIEnv* env, jclass clazz, jstring videoPath);

// Scene Detection
JNIEXPORT jobject JNICALL
Java_com_example_gallery_native_NativeBridge_nativeDetectScene(
    JNIEnv* env, jclass clazz, jobject bitmap);

// Object Detection
JNIEXPORT jobjectArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeDetectObjects(
    JNIEnv* env, jclass clazz, jobject bitmap);

// Text Detection (OCR)
JNIEXPORT jobjectArray JNICALL
Java_com_example_gallery_native_NativeBridge_nativeDetectText(
    JNIEnv* env, jclass clazz, jobject bitmap);

}

// Helper functions for JNI conversions
namespace jni_utils {

// Convert Java Bitmap to native pixel buffer
bool bitmapToPixels(JNIEnv* env, jobject bitmap, AndroidBitmapInfo* info, void** pixels);

// Create Java Bitmap from native pixel buffer
jobject createBitmap(JNIEnv* env, int width, int height, const void* pixels, int format);

// Convert jfloatArray to std::vector<float>
std::vector<float> floatArrayToVector(JNIEnv* env, jfloatArray array);

// Convert std::vector<float> to jfloatArray
jfloatArray vectorToFloatArray(JNIEnv* env, const std::vector<float>& vec);

// Convert jintArray to std::vector<int>
std::vector<int> intArrayToVector(JNIEnv* env, jintArray array);

// Convert std::vector<int> to jintArray
jintArray vectorToIntArray(JNIEnv* env, const std::vector<int>& vec);

// Create Java Rect object
jobject createRect(JNIEnv* env, int left, int top, int right, int bottom);

// Create Java ClassificationResult object
jobject createClassificationResult(JNIEnv* env, const ClassificationResult& result);

// Create Java FaceDetectionResult object
jobject createFaceDetectionResult(JNIEnv* env, const FaceDetectionResult& result);

// Create Java SceneDetectionResult object
jobject createSceneDetectionResult(JNIEnv* env, const SceneDetectionResult& result);

// Create Java ObjectDetectionResult object
jobject createObjectDetectionResult(JNIEnv* env, const ObjectDetectionResult& result);

// Create Java TextDetectionResult object
jobject createTextDetectionResult(JNIEnv* env, const TextDetectionResult& result);

// Throw Java exception
void throwException(JNIEnv* env, const char* className, const char* message);

} // namespace jni_utils

#endif // GALLERY_NATIVE_BRIDGE_H