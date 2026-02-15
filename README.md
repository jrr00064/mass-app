# MASS - Mindful Application Screen-time System

A Nothing Phone app that visualizes digital wellbeing through the Glyph Matrix.

## Overview

MASS tracks your app usage and displays your "digital mass" on the Nothing Phone's Glyph Matrix. The more you use high-distraction apps, the denser the glyph pattern becomes.

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
                    │   Renderer      │
                    └─────────────────┘
```

## Features

- **Weighted Categories**: Social (1.5x), Gaming (1.3x), Productivity (0.6x), etc.
- **Recency Decay**: Recent usage counts more than old usage
- **Visual States**: LIGHT (0-60%), DENSE (60-90%), CRITICAL (90-100%)
- **33-Dot Matrix**: Circular spiral pattern from center outward
- **Smooth Animations**: 500ms transitions with cubic easing

## Tech Stack

- Kotlin 1.9.20
- Android API 34 (min SDK 30)
- Kotlin Coroutines 1.7.3
- Nothing Glyph SDK (GlyphMatrix)

## Building

```bash
# Requires Nothing Glyph SDK .aar in app/libs/
./gradlew assembleDebug
```

## Permissions

- `PACKAGE_USAGE_STATS` - Required for app usage tracking
- `FOREGROUND_SERVICE` - Keeps service running

## License

MIT
