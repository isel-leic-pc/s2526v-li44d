# Milestone 2 Detailed Task List (Draft)

## Purpose

Milestone 2 introduces the first end-to-end image-processing flow on top of the Milestone 1 loading pipeline:

- a processing-pipeline abstraction
- a thread-based pipeline implementation
- the first two filters (`grayscale`, `brightness`)
- live processed preview driven by explicit/testable ViewModel transitions

This milestone must not yet implement coroutine processing (Milestone 3) or export (Milestone 4).

---

## Deliverables

- `ImagePipeline` contract for reusable filter execution.
- `ThreadedImagePipeline` implementation using data partitioning with JVM threads.
- First filter model + algorithms:
  - grayscale (toggle/on-off)
  - brightness (toggle + numeric parameter)
- ViewModel flow updated to render processed preview from filter configuration.
- UI controls to toggle/configure the two filters.
- Tests for filter correctness, pipeline behavior, and state-machine transitions related to processing.

---

## Task Breakdown

## 1. Define Milestone 2 Domain Model for Processing

- [x] Introduce explicit domain types for processing configuration (no raw maps/flags in UI code).
- [x] Model filter configuration as immutable types with parse/constructor invariants.
- [x] Define defaults for first filters:
  - grayscale disabled by default
  - brightness disabled by default with neutral parameter value
- [x] Keep types ready for extension in Milestones 3/4 (contrast/blur), without implementing those filters now.

Acceptance criteria:

- Invalid filter states are not representable through constructors/parsers.
- Filter configuration can be passed as a single immutable value from ViewModel to processing layer.

---

## 2. Define `ImagePipeline` Abstraction

- [ ] Add a processing contract that receives:
  - source image
  - active filter configuration
  - optional execution settings needed for thread partitioning
- [ ] Define deterministic processing semantics (same input/config -> same output).
- [ ] Keep contract independent from UI concerns so both thread and coroutine implementations can share it later.

Acceptance criteria:

- One pipeline contract can be consumed by ViewModel without knowing implementation details.
- API shape is directly reusable by Milestone 3 coroutine pipeline.

---

## 3. Implement Initial Filters (Grayscale + Brightness)

- [ ] Implement grayscale as a deterministic per-pixel transformation.
- [ ] Implement brightness with parameter clamping and neutral no-op behavior.
- [ ] Enforce predictable filter composition order for Milestone 2.
- [ ] Add small reusable pixel utilities for channel clamping/conversion as needed.

Acceptance criteria:

- Each filter works independently and can be combined with deterministic output.
- Brightness parameter extremes remain safe (no channel overflow/underflow).

---

## 4. Implement `ThreadedImagePipeline`

- [ ] Implement data-parallel partitioning strategy (e.g., row chunks).
- [ ] Process chunks on worker threads and merge into a single output image.
- [ ] Ensure processing is off the UI thread and does not mutate input image buffers.
- [ ] Provide clear error propagation for processing failures.

Acceptance criteria:

- Pipeline returns processed image for valid inputs.
- Processing output is equivalent to single-thread expected logic for the same config.

---

## 5. Extend ViewModel State Machine for Processed Preview

- [ ] Extend screen state payloads to include original + processed image roles explicitly.
- [ ] Add ViewModel operations for:
  - toggling grayscale
  - toggling brightness
  - setting brightness parameter
  - resetting filters to defaults
- [ ] Define transition rules for processing lifecycle (idle/rendering/ready/error), preserving explicit fallback behavior.
- [ ] Trigger recomputation when filters change and an image is loaded.

Acceptance criteria:

- Filter changes from `Ready` trigger processing transitions and update processed preview.
- Invalid operations from incompatible states are handled consistently with current state-machine policy.

---

## 6. Wire Milestone 2 UI Controls and Preview

- [ ] Replace filter placeholder area with concrete controls for grayscale and brightness.
- [ ] Bind controls to ViewModel operations (no local business logic in composables).
- [ ] Show processed image preview separately from original image preview.
- [ ] Keep controls disabled/guarded when no image is loaded or during invalid states.

Acceptance criteria:

- User can load image, toggle filters, adjust brightness, and see processed preview updates.
- UI remains responsive while processing is running.

---

## 7. Add Tests for Processing and New Transitions

- [ ] Add unit tests for grayscale correctness on small synthetic images.
- [ ] Add unit tests for brightness behavior (neutral, increase, decrease, clamp bounds).
- [ ] Add threaded pipeline tests for deterministic output and composition order.
- [ ] Extend ViewModel tests for:
  - filter-toggle recomputation path
  - brightness parameter updates
  - reset-filters behavior
  - processing error to recoverable state behavior
- [ ] Keep tests AAA and one behavior/property per test.

Acceptance criteria:

- Test suite covers Milestone 2 core behavior without depending on Milestone 3/4 features.
- No tests rely on template-era assumptions or UI-only behavior.

---

## 8. Final Verification Checklist (Milestone 2 Exit)

- [ ] `./gradlew :composeApp:compileKotlinJvm` passes.
- [ ] `./gradlew :composeApp:jvmTest` passes.
- [ ] Manual run confirms:
  - `Open` image
  - toggle grayscale on/off
  - adjust brightness
  - processed preview updates consistently
- [ ] Manual checks confirm thread-based processing does not block UI interaction.
- [ ] `Coroutines` mode remains explicitly non-processing/placeholder for now (to be completed in Milestone 3).

Exit condition for Milestone 2:

- Milestone 1 image loading flow is preserved, and first filters run in live preview through the thread-based processing pipeline with explicit/tested state transitions.

---

## Suggested Execution Order

1. Domain model for filter configuration and invariants.
2. Filter algorithms + shared pixel utilities.
3. `ImagePipeline` contract.
4. `ThreadedImagePipeline` implementation.
5. ViewModel transition extension for processing events.
6. UI controls + processed preview wiring.
7. Tests.
8. Verification checklist.
