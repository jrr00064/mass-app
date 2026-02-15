# MASS - Mindful Application Screen-time System

A Nothing Phone 3 app that visualizes digital wellbeing through the Glyph Matrix (25x25 LED array).

## Overview

MASS tracks your app usage and displays your "digital mass" on the Nothing Phone 3's Glyph Matrix. The more you use high-distraction apps, the more the 25x25 matrix fills from center outward.

## Architecture

```
┌──────────────────┐    ┌──────────────────┐
│  UsageTracker    │───▶│  MassCalculator  │
│     Service      │    │     Engine       │
└──────────────────┘    └─────────┬────────┘
                                  │
                                  ▼
                    ┌─────────────────┐
                    │  MassDataModel  │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ GlyphController │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ GlyphMatrix     │
                    │   (25x25)       │
                    └─────────────────┘
```

## Features

- **Weighted Categories**: Social (1.5x), Gaming (1.3x), Productivity (0.6x), etc.
- **Recency Decay**: Recent usage counts more than old usage
- **Visual States**: LIGHT (0-60%), DENSE (60-90%), CRITICAL (90-100%)
- **25x25 Matrix**: Circular fill pattern from center outward
- **Smooth Animations**: 500ms transitions with cubic easing

## Glyph Matrix API (Nothing Phone 3)

This app uses the official **GlyphMatrix Developer Kit** for Nothing Phone 3:

- `GlyphMatrixManager` - Connection and display management
- `GlyphMatrixFrame` - 25x25 frame builder with layers (Top/Mid/Low)
- `GlyphMatrixObject` - Bitmap objects with position, scale, brightness
- `Glyph.DEVICE_23112` - Phone 3 device identifier

## Tech Stack

- Kotlin 1.9.20
- Android API 34 (min SDK 30 - Nothing OS 4+)
- Kotlin Coroutines 1.7.3
- Nothing GlyphMatrix SDK

## Required Permissions

- `PACKAGE_USAGE_STATS` - App usage tracking
- `FOREGROUND_SERVICE` - Background service
- `com.nothing.ketchum.permission.ENABLE` - Glyph Matrix access

## Building

```bash
# 1. Download GlyphMatrixSDK.aar from Nothing Developer Kit
#    https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit
# 2. Place in app/libs/
cp GlyphMatrixSDK.aar app/libs/

# 3. Build
./gradlew assembleDebug
```

## Documentation

- [GlyphMatrix Developer Kit](https://github.com/Nothing-Developer-Programme/GlyphMatrix-Developer-Kit)
- [Nothing Community](https://nothing.community/t/glyph-sdk)

## License

MIT
