# ImageViewer Implementation Plan (No Code Yet)

## 1. Goal

Build a desktop demo application (Kotlin + Compose Multiplatform, JVM target) for a Concurrent Programming course that:

- Opens local image files.
- Displays original and processed images.
- Applies simple image filters with adjustable parameters.
- Demonstrates data-parallel image processing using multiple concurrency approaches.
- Exports the processed result as PNG.

This plan defines architecture and milestones before implementation.

---

## 2. Scope (V1)

### In scope

- Single-window desktop UI.
- Open image from disk (PNG/JPEG input).
- Live preview while changing filter settings.
- Filter set based on simple algorithms suitable for data-parallel demonstration.
- Toggle filters on/off.
- Reset to original image.
- Save processed image to disk (PNG).
- Two processing implementations for comparison:
  - JVM traditional threading model.
  - Kotlin coroutines model.

### Out of scope

- Filter reordering.
- Zoom.
- Batch processing.
- GPU/shader-based processing.
- Plugin architecture.

---

## 3. Product Behavior

### Primary user flow

1. User opens an image file.
2. App shows image preview and filter controls.
3. User enables filters and changes parameter sliders.
4. Preview updates quickly and predictably.
5. User saves result as a new PNG file.

### UX expectations

- UI remains responsive while recomputation runs in background.
- Render status is visible (`Idle`, `Rendering`, `Error`).
- Numeric parameters are validated and clamped.
- Save flow avoids accidental overwrite by default.

---

## 4. Architecture (MVVM + State Hoisting)

### 4.1 Architectural style

- Use MVVM.
- Model UI as a state machine.
- Keep composables stateless where possible by hoisting state.
- Use the ViewModel as the orchestrator between UI and processing components.

### 4.2 Layers

- UI layer (Compose):
  - Window layout
  - Preview panels
  - Filter controls
  - File actions (open/save/reset)
- ViewModel/state layer:
  - UI state model and transitions
  - Event handling (`OpenImage`, `SetFilterParam`, `ToggleFilter`, `SaveImage`, `Reset`)
  - Interaction with pipeline implementations
- Processing layer:
  - Filter contract and implementations
  - Pipeline abstractions and concrete implementations
  - Image I/O and format conversion utilities

### 4.3 Core state and domain model

- `ImageViewerUiState`
  - current file metadata
  - original image
  - processed image
  - filter configurations
  - render status
  - selected processing mode
- `FilterConfig` (sealed model per filter type)
  - enabled flag
  - filter parameters
- `RenderState`
  - `Idle | Rendering | Success | Error(message)`
- `ProcessingMode`
  - `Threads | Coroutines`

### 4.4 Concurrency strategy

- Run processing off UI thread for both modes.
- Provide two interchangeable pipeline implementations:
  - `ThreadedImagePipeline` (traditional JVM threads/data partitioning).
  - `CoroutineImagePipeline` (structured concurrency with coroutines).
- Cancel stale render tasks when new settings arrive.
- Debounce rapid slider changes.

---

## 5. Filter Design

### 5.1 Filter contract

Define a single filter interface:

- Input image buffer
- Filter parameters
- Output image buffer

This keeps pipelines interchangeable and simplifies testing.

### 5.2 Filter algorithms

- Use simple, understandable algorithms appropriate for teaching data parallelism.
- Prefer deterministic, pixel-local or neighborhood-based operations.
- Suggested initial set:
  - Grayscale
  - Brightness
  - Contrast
  - Blur (small kernel/radius)

### 5.3 Color model

- Operate directly on sRGB channel integers for V1 simplicity and speed.
- No linear-color conversion in V1.

---

## 6. UI Plan

### 6.1 Layout

- Top bar: `Open`, `Save As (PNG)`, `Reset`.
- Main split:
  - Left: original and processed previews.
  - Right: processing mode selector + filter controls.
- Footer/status row: file name, resolution, render status, and optional timing.

### 6.2 Controls

- Processing mode selector (`Threads` / `Coroutines`).
- Per-filter card:
  - enable toggle
  - slider(s) with numeric value labels
  - reset filter button
- Global controls:
  - reset all filters
  - optional compare toggle (`Original` vs `Processed`)

### 6.3 Error handling UX

- Non-blocking error message for:
  - invalid/unsupported input file
  - load failures
  - save failures

---

## 7. Project Structure (Target)

Suggested package breakdown under
`composeApp/src/jvmMain/kotlin/palbp/demos/pc/isel/imageviewer`:

- `ui/`
  - `AppScreen.kt`
  - `components/`
- `viewmodel/`
  - `ImageViewerViewModel.kt`
  - `ImageViewerUiState.kt`
  - `ImageViewerEvent.kt`
- `domain/`
  - `FilterConfig.kt`
  - `RenderState.kt`
  - `ProcessingMode.kt`
- `processing/`
  - `ImagePipeline.kt`
  - `ThreadedImagePipeline.kt`
  - `CoroutineImagePipeline.kt`
  - `filters/`
  - `ImageIo.kt`
- `util/`
  - conversion/clamping helpers

Tests under `composeApp/src/jvmTest/...`, mirroring `viewmodel/` and `processing/`.

---

## 8. Milestones

### Milestone 0: Replace template with app shell

- Remove template greeting UI.
- Create base screen layout.
- Add hoisted UI state model and event wiring.

Exit criteria:

- Window launches with functional shell and placeholder content.

### Milestone 1: MVVM state machine + image loading

- Implement ViewModel with state machine transitions.
- Integrate file chooser.
- Load and display PNG/JPEG images.

Exit criteria:

- User can open an image and UI state transitions are explicit/testable.

### Milestone 2: First pipeline and first filters

- Implement `ImagePipeline` abstraction.
- Implement thread-based pipeline.
- Implement initial filters (grayscale, brightness).

Exit criteria:

- Filters apply in live preview using threaded pipeline.

### Milestone 3: Coroutine pipeline

- Implement coroutine-based pipeline with same filter contract.
- Add processing mode switch in UI.

Exit criteria:

- User can switch between `Threads` and `Coroutines` with equivalent visual output.

### Milestone 4: Complete V1 filter set + export

- Add remaining simple filters (e.g., contrast, blur).
- Add parameter clamping/defaults.
- Implement `Save As` PNG flow.

Exit criteria:

- Planned filters work and processed result exports to PNG.

### Milestone 5: Validation and course-demo polish

- Add tests for filter correctness and state transitions.
- Add tests comparing outputs across processing modes.
- Improve responsiveness (debounce/cancellation).

Exit criteria:

- App is stable and clearly demonstrates concurrency-model differences.

---

## 9. Testing Strategy

### Unit tests

- Per-filter correctness on small synthetic images.
- State machine transition tests in ViewModel.
- Parameter clamping and edge-case tests.

### Equivalence tests

- Same input + same filter config should produce equivalent output for:
  - `ThreadedImagePipeline`
  - `CoroutineImagePipeline`

### Integration tests (lightweight)

- Open image -> apply filters -> save PNG happy path.
- Failure paths for invalid load/save operations.

### Manual checks

- Responsiveness while dragging sliders.
- Visual sanity at parameter extremes.
- Behavior on larger images.

---

## 10. Risks and Mitigations

- UI freezes under heavy recomputation:
  - run all processing off UI thread and cancel stale jobs.
- Divergence between pipeline implementations:
  - maintain one shared filter contract and add equivalence tests.
- Race conditions during rapid input changes:
  - use explicit state transitions plus structured cancellation.

---

## 11. Decisions Locked for V1

1. This is a course demo focused on concurrency and data parallelism.
2. Filter algorithms will be simple rather than photorealistically accurate.
3. Filter reordering is excluded.
4. Zoom is excluded.
5. Export format is PNG only.
