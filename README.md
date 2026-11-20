# Material Expressive Gallery

A modern, Material Expressive (Material 3) based gallery app inspired by Google Photos Go, built with Jetpack Compose, Hilt, Room, and native C++ image processing.

## Features

### Core Gallery Features
- **Photo Grid** - LazyVerticalGrid with Paging 3, dynamic column spanning
- **Section Tabs** - Photos, Favorites, Videos, Screenshots, Documents, Device Folders
- **Memories** - "On this day", Recent Highlights, Trips, Celebrations
- **Selection Mode** - Multi-select with batch operations (share, delete, favorite, add to album)
- **Media Viewer** - Full-screen photo/video viewing with zoom/pan

### Search (Material Expressive)
- **Expressive Search Bar** - Hero element with dynamic elevation, voice search, filters
- **Smart Suggestions** - People, Places, Things, Dates, Categories, Creations
- **Recent Searches** - Persisted history with clear option
- **Filter Chips** - Expandable media type, favorites, location, people filters
- **Combined Search** - Media items, face groups, albums, history in single query

### People & Pets
- Face detection and grouping (FaceNet embeddings)
- Person detail view with photos
- Naming and hiding faces

### Albums
- User-created albums
- Auto-generated albums (People, Places, Things, Videos, etc.)
- Album detail with grid view

### Editor
- Filters and LUTs
- Adjustments (brightness, contrast, saturation, etc.)
- Crop/Rotate
- Markup tools
- Video trim and stabilize

### Locked Folder
- Biometric authentication (fingerprint/face)
- Move photos/videos to secure storage
- Encrypted storage

### Native Performance (C++)
- Face detection (BlazeFace)
- Face embedding extraction (FaceNet/MobileFaceNet)
- Image classification (EfficientNet/MobileNet)
- Blur detection
- Aesthetic scoring (NIMA)
- Dominant color extraction
- Auto-enhance
- Scene detection
- Object detection
- Text detection (OCR)

## Architecture

```
app/
├── src/main/
│   ├── java/com/example/gallery/
│   │   ├── data/
│   │   │   ├── local/          # Room Database, DAOs, TypeConverters
│   │   │   └── repository/     # MediaRepository (search, queries)
│   │   ├── di/                 # Hilt Modules (Database, Repository)
│   │   ├── domain/
│   │   │   └── model/          # MediaItem, FaceGroup, Album, SearchResult, etc.
│   │   ├── native/             # JNI Bridge (NativeBridge.kt)
│   │   ├── ui/
│   │   │   ├── home/           # HomeScreen, HomeViewModel
│   │   │   ├── search/         # SearchScreen, SearchViewModel, SearchActivity
│   │   │   └── MainActivity.kt # Navigation host
│   │   ├── GalleryApplication.kt
│   │   └── ui/theme/           # Material Expressive Theme (Color, Type, Theme)
│   ├── cpp/                    # Native C++ Implementation
│   │   ├── include/            # Headers
│   │   └── src/                # Implementation
│   └── res/                    # Resources (colors, strings, themes, xml)
└── build.gradle.kts
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| UI | Jetpack Compose, Material 3 Expressive |
| DI | Hilt |
| Database | Room (Kotlin Flow) |
| Image Loading | Coil 2.4 |
| Navigation | Navigation Compose |
| Background Work | WorkManager |
| ML | ML Kit, TensorFlow Lite, Native C++ |
| Video | ExoPlayer |
| Serialization | Kotlinx Serialization |
| Async | Coroutines, Flow |
| Paging | Paging 3 Compose |

## Android Go Optimizations

- **minSdk 24** - Wide device compatibility
- **Split APKs** - Density (mdpi-xxxhdpi) and ABI (arm64, armeabi)
- **MultiDex** - Enabled
- **R8/ProGuard** - Aggressive shrinking
- **Lightweight ML** - ML Kit on-device, TFLite GPU delegate
- **Native C++** - Performance-critical paths in JNI
- **Paging 3** - Efficient large dataset loading
- **Coil** - Memory-efficient image loading with disk cache

## Material Expressive Design

### Color Palette
- **Primary**: Coral (#FF321E) - Warm, expressive
- **Secondary**: Teal (#00BB9E) - Fresh, modern
- **Tertiary**: Purple (#9333EA) - Creative, playful

### Typography
- **Hero** (72sp) - Splash, empty states
- **Celebration** (48sp) - Achievements
- **SectionHeader** (20sp) - Section titles
- **SearchQuery** (16sp) - Search input
- **Metadata** (12sp) - Dates, locations
- **NumberLarge** (48sp) - Stats

### Components
- Expressive Search Bar (hero element)
- Dynamic elevation toolbars
- Animated selection mode
- Memory cards with badges
- Filter chips with icons
- Floating Action Button with label

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build (with ProGuard)
./gradlew assembleRelease

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

## Project Structure Details

### Domain Models
- `MediaItem` - Core media entity with ML labels, face groups, colors, scores
- `FaceGroup` - Clustered faces with name, cover, count
- `Face` - Individual face with bounding box, embedding, landmarks
- `Album` - User/auto albums with media references
- `SearchResult` - Combined search results with suggestions
- `SearchSuggestion` - Typed suggestions (People, Place, Thing, Date, Category, Creation)
- `SearchHistory` - Persisted queries with timestamps

### Repository Queries
- Paged media loading (100 items)
- Bucket/folder grouping
- Favorites, trash, date ranges
- Location-based queries
- Face group queries
- Label-based classification queries
- Full-text search across name, location, labels

### Native Bridge (JNI)
```kotlin
// Initialize at app startup
NativeBridge.initialize(context)

// Face detection
val faces = NativeBridge.detectFaces(bitmap)

// Face embedding (512-dim)
val embedding = NativeBridge.getFaceEmbedding(alignedFace)

// Compare faces (cosine similarity)
val similarity = NativeBridge.compareFaces(emb1, emb2)

// Image classification
val labels = NativeBridge.classifyImage(bitmap)

// Quality scores
val blurScore = NativeBridge.detectBlur(bitmap)
val aestheticScore = NativeBridge.scoreAesthetic(bitmap)

// Colors
val colors = NativeBridge.extractDominantColors(bitmap)

// Enhancement
val enhanced = NativeBridge.autoEnhance(bitmap)
```

## Search Implementation

Based on Google Photos Go's Filter categories:
- **People** - Face groups with names
- **Places** - Location names from EXIF
- **Things** - ML labels (dog, beach, food, etc.)
- **Categories** - Screenshots, Documents, Videos, etc.
- **Dates** - Year, Month, Day, Relative (recent, today)
- **Creations** - Collages, Movies, Animations, Albums

The `MediaRepository.searchAll()` combines:
1. Media items (name, location, labels)
2. Face groups (names)
3. Albums (titles)
4. Search history (recent queries)

## License

Apache 2.0 - See LICENSE file

## Acknowledgements

- Google Photos Go for architecture inspiration
- Material Design 3 Expressive for design system
- TensorFlow Lite for on-device ML
- ML Kit for ready-to-use ML APIs