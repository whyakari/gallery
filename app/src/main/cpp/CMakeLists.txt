cmake_minimum_required(VERSION 3.18.1)
project("gallery_native" LANGUAGES C CXX)

# Android NDK settings
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Build type configurations
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    set(CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE} -O3 -DNDEBUG")
    set(CMAKE_C_FLAGS_RELEASE "${CMAKE_C_FLAGS_RELEASE} -O3 -DNDEBUG")
elseif(CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O0 -g -DDEBUG")
    set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -O0 -g -DDEBUG")
endif()

# Include directories
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/src
)

# Source files
set(NATIVE_SOURCES
    src/native_bridge.cpp
    src/face_detection.cpp
    src/face_embedding.cpp
    src/image_classification.cpp
    src/blur_detection.cpp
    src/aesthetic_scoring.cpp
    src/color_extraction.cpp
    src/auto_enhance.cpp
    src/video_analysis.cpp
    src/scene_detection.cpp
    src/object_detection.cpp
    src/text_detection.cpp
    src/jni_utils.cpp
    src/tflite_runner.cpp
)

# Create shared library
add_library(gallery_native SHARED ${NATIVE_SOURCES})

# Link libraries
target_link_libraries(gallery_native
    android
    log
    jnigraphics
    mediandk
    tensorflowlite_jni
    tensorflowlite_gpu_delegate
)

# TensorFlow Lite library paths
# These should be provided by the TensorFlow Lite Gradle plugin or manually
# find_library(TFLITE_LIB tensorflowlite_jni)
# find_library(TFLITE_GPU_LIB tensorflowlite_gpu_delegate)

# Android NDK Media APIs for image processing
target_link_libraries(gallery_native android mediandk)

# Compiler flags for optimization
target_compile_options(gallery_native PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
    -fvisibility=hidden
    -ffunction-sections
    -fdata-sections
)

# Linker flags
target_link_options(gallery_native PRIVATE
    -Wl,--gc-sections
    -Wl,--strip-all
)

# Install headers
install(FILES
    include/native_bridge.h
    DESTINATION include
)