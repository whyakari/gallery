package com.example.gallery.native

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log

/**
 * JNI Bridge for native image processing operations.
 * Mirrors the libnative.so functionality from Google Photos Go.
 *
 * Native functions include:
 * - Face detection and alignment
 * - Face embedding extraction (FaceNet)
 * - Image classification (labels)
 * - Blur detection
 * - Aesthetic scoring
 * - Color extraction
 * - Image enhancement (auto-enhance)
 * - Video stabilization analysis
 */
class NativeBridge private constructor() {

    companion object {
        private const val TAG = "NativeBridge"
        private var sInitialized = false

        @JvmStatic
        fun initialize(context: Context): Boolean {
            if (sInitialized) return true

            try {
                System.loadLibrary("gallery_native")
                nativeInit(context.assets)
                sInitialized = true
                Log.i(TAG, "Native library loaded successfully")
                return true
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library", e)
                return false
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing native bridge", e)
                return false
            }
        }

        @JvmStatic
        fun isInitialized(): Boolean = sInitialized

        // Face Detection
        @JvmStatic
        fun detectFaces(bitmap: Bitmap, minFaceSize: Float = 0.1f): Array<FaceDetectionResult>? {
            checkInitialized()
            return nativeDetectFaces(bitmap, minFaceSize)
        }

        // Face Alignment (for FaceNet embedding)
        @JvmStatic
        fun alignFace(bitmap: Bitmap, landmarks: FloatArray): Bitmap? {
            checkInitialized()
            return nativeAlignFace(bitmap, landmarks)
        }

        // Face Embedding (FaceNet - 512 dim)
        @JvmStatic
        fun getFaceEmbedding(alignedFace: Bitmap): FloatArray? {
            checkInitialized()
            return nativeGetFaceEmbedding(alignedFace)
        }

        // Face Comparison (cosine similarity)
        @JvmStatic
        fun compareFaces(embedding1: FloatArray, embedding2: FloatArray): Float {
            checkInitialized()
            return nativeCompareFaces(embedding1, embedding2)
        }

        // Image Classification (labels: "dog", "beach", "food", etc.)
        @JvmStatic
        fun classifyImage(bitmap: Bitmap, maxResults: Int = 10): Array<ClassificationResult>? {
            checkInitialized()
            return nativeClassifyImage(bitmap, maxResults)
        }

        // Blur Detection (0.0 = blurry, 1.0 = sharp)
        @JvmStatic
        fun detectBlur(bitmap: Bitmap): Float {
            checkInitialized()
            return nativeDetectBlur(bitmap)
        }

        // Aesthetic Scoring (0.0 - 1.0)
        @JvmStatic
        fun scoreAesthetic(bitmap: Bitmap): Float {
            checkInitialized()
            return nativeScoreAesthetic(bitmap)
        }

        // Dominant Color Extraction
        @JvmStatic
        fun extractDominantColors(bitmap: Bitmap, colorCount: Int = 5): IntArray? {
            checkInitialized()
            return nativeExtractDominantColors(bitmap, colorCount)
        }

        // Auto Enhance (returns enhanced bitmap)
        @JvmStatic
        fun autoEnhance(bitmap: Bitmap): Bitmap? {
            checkInitialized()
            return nativeAutoEnhance(bitmap)
        }

        // Video Stabilization Score
        @JvmStatic
        fun analyzeVideoStabilization(videoPath: String): Float {
            checkInitialized()
            return nativeAnalyzeVideoStabilization(videoPath)
        }

        // Scene Detection
        @JvmStatic
        fun detectScene(bitmap: Bitmap): SceneDetectionResult? {
            checkInitialized()
            return nativeDetectScene(bitmap)
        }

        // Object Detection (for Google Lens-like features)
        @JvmStatic
        fun detectObjects(bitmap: Bitmap): Array<ObjectDetectionResult>? {
            checkInitialized()
            return nativeDetectObjects(bitmap)
        }

        // Text Detection (OCR)
        @JvmStatic
        fun detectText(bitmap: Bitmap): Array<TextDetectionResult>? {
            checkInitialized()
            return nativeDetectText(bitmap)
        }

        // Cleanup
        @JvmStatic
        fun release() {
            if (sInitialized) {
                nativeRelease()
                sInitialized = false
            }
        }

        private fun checkInitialized() {
            if (!sInitialized) {
                throw IllegalStateException("NativeBridge not initialized. Call initialize() first.")
            }
        }

        // Native method declarations
        external fun nativeInit(assets: android.content.res.AssetManager)
        external fun nativeDetectFaces(bitmap: Bitmap, minFaceSize: Float): Array<FaceDetectionResult>?
        external fun nativeAlignFace(bitmap: Bitmap, landmarks: FloatArray): Bitmap?
        external fun nativeGetFaceEmbedding(alignedFace: Bitmap): FloatArray?
        external fun nativeCompareFaces(embedding1: FloatArray, embedding2: FloatArray): Float
        external fun nativeClassifyImage(bitmap: Bitmap, maxResults: Int): Array<ClassificationResult>?
        external fun nativeDetectBlur(bitmap: Bitmap): Float
        external fun nativeScoreAesthetic(bitmap: Bitmap): Float
        external fun nativeExtractDominantColors(bitmap: Bitmap, colorCount: Int): IntArray?
        external fun nativeAutoEnhance(bitmap: Bitmap): Bitmap?
        external fun nativeAnalyzeVideoStabilization(videoPath: String): Float
        external fun nativeDetectScene(bitmap: Bitmap): SceneDetectionResult?
        external fun nativeDetectObjects(bitmap: Bitmap): Array<ObjectDetectionResult>?
        external fun nativeDetectText(bitmap: Bitmap): Array<TextDetectionResult>?
        external fun nativeRelease()
    }
}

// Data classes for native results
data class FaceDetectionResult(
    val boundingBox: Rect,
    val confidence: Float,
    val landmarks: FloatArray, // [leftEyeX, leftEyeY, rightEyeX, rightEyeY, noseX, noseY, mouthX, mouthY]
    val yaw: Float,
    val pitch: Float,
    val roll: Float,
)

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val category: String, // "thing", "place", "activity", etc.
)

data class SceneDetectionResult(
    val sceneType: String, // "indoor", "outdoor", "portrait", "landscape", "night", etc.
    val confidence: Float,
    val attributes: Map<String, Float>, // "sunny": 0.9, "beach": 0.8, etc.
)

data class ObjectDetectionResult(
    val boundingBox: Rect,
    val label: String,
    val confidence: Float,
    val category: String,
)

data class TextDetectionResult(
    val boundingBox: Rect,
    val text: String,
    val confidence: Float,
    val language: String?,
)