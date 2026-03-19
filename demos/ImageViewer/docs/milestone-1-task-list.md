# Milestone 1 Detailed Task List (Draft)

## Purpose

Milestone 1 will complete the first end-to-end usable flow: select a local image file, load it, and display it in the app using explicit/testable MVVM state transitions.

Given the current codebase, this milestone focuses on replacing the current stubbed `"imageName"` load path with real file/image loading.

---

## Deliverables

- File picker integrated into `Open`.
- Real PNG/JPEG decoding from disk.
- ViewModel state machine carrying real loaded image data (not just file name).
- UI preview rendering the loaded image.
- Error handling for invalid/corrupt/unsupported files.
- Tests updated/added for new load flow and transitions.

---

## Task Breakdown

## 1. Define Milestone 1 Domain Model Adjustments

- [x] Refine `ImageViewerScreenState` payloads to carry real image data and file metadata needed by UI.
- [x] Keep transition states explicit (`NoImage`, `LoadingImage`, `Ready`, `Error`) and preserve fallback behavior.
- [x] Decide and document image representation for VM/UI boundary (e.g., JVM image type + UI-ready conversion strategy).

Acceptance criteria:

- State is sufficient to render loaded image preview and footer metadata.
- No UI component needs to infer/load image data on its own.

Status:

- Completed on 2026-03-19.
- Representation decision for current milestone phase:
  - `LoadedImage` now carries Compose-native `ImageBitmap` and `ImageMetadata`.
- Verification:
  - `export JAVA_HOME=$(/usr/libexec/java_home -v 21) && ./gradlew :composeApp:jvmTest`

---

## 2. Implement Image Loading Service (JVM)

- [x] Add a JVM image-loading component that:
  - validates supported extensions (`.png`, `.jpg`, `.jpeg`)
  - decodes image file content
  - returns loaded image + metadata (name/path/dimensions)
- [x] Normalize error mapping to user-facing messages.
- [x] Keep API shape compatible with both thread and coroutine ViewModels.

Acceptance criteria:

- Valid PNG/JPEG files load successfully.
- Corrupt or unsupported files fail with controlled errors.

Status:

- Completed on 2026-03-19.
- Added JVM loader component:
  - `processing/LocalFileImageLoader.kt`
  - `processing/ImageLoader.kt` (abstraction)
- Error mapping currently covers:
  - unsupported extension
  - file-not-found/read failures
  - decode failures
- VM integration is intentionally deferred to Task 4.

---

## 3. Integrate Desktop File Chooser with `Open`

- [ ] Replace placeholder `onOpen` behavior in `App.kt` with real file chooser invocation.
- [ ] Apply file filters for PNG/JPEG.
- [ ] Handle cancel action as no-op (no state mutation).
- [ ] Dispatch selected file to active ViewModel load operation.

Acceptance criteria:

- User can pick an image from disk through `Open`.
- Canceling picker does not alter current state.

---

## 4. Update ViewModel Load Path to Real Inputs

- [ ] Change `requestLoadImage(...)` contract from fake image name to file-based input.
- [ ] Keep transition rules explicit and testable:
  - `NoImage/Ready -> LoadingImage -> Ready|Error`
  - `dismissError` returns to fallback
  - `reset` returns to `NoImage`
- [ ] Ensure both `ThreadsImageViewerViewModel` and `CoroutinesImageViewerViewModel` use the same load semantics.

Acceptance criteria:

- Both implementations exhibit equivalent state-machine behavior for load success/failure.
- No regression in existing transition invariants.

---

## 5. Render Real Image in UI

- [ ] Update preview area to display loaded image instead of placeholder text when `Ready`.
- [ ] Keep placeholder content for `NoImage`/`LoadingImage`.
- [ ] Ensure image display scales safely in preview container (no layout break on large images).

Acceptance criteria:

- Loaded image is visible in preview panel.
- UI remains responsive and stable while loading.

---

## 6. Footer/Status Metadata Wiring

- [ ] Show meaningful loaded-file metadata in footer (at minimum file name and resolution).
- [ ] Keep status rendering aligned with state machine (`Idle/Loading/Error/Ready` semantics).
- [ ] Preserve dismiss-error UX path.

Acceptance criteria:

- Footer information reflects current loaded image and current render/load state accurately.

---

## 7. Tests for Milestone 1 Behavior

- [ ] Update current ViewModel tests to new load-input type and state payloads.
- [ ] Add tests for:
  - successful load from `NoImage`
  - successful load from `Ready` with correct fallback capture
  - decode failure -> `Error` + fallback
  - `dismissError` and `reset` invariants
- [ ] Add focused tests for image-loader component (supported/unsupported/corrupt file cases).

Acceptance criteria:

- Test suite covers the complete Milestone 1 state/load behavior.
- No template-era assumptions remain in tests.

---

## 8. Final Verification Checklist (Milestone 1 Exit)

- [ ] `./gradlew :composeApp:compileKotlinJvm` passes.
- [ ] `./gradlew :composeApp:jvmTest` passes.
- [ ] Manual run confirms: `Open` -> pick PNG/JPEG -> image preview appears.
- [ ] Manual error checks confirm: invalid/corrupt file shows non-blocking error and can be dismissed.
- [ ] No hardcoded sample-image loading path remains.

Exit condition for Milestone 1:

- User can open and view real local PNG/JPEG files with explicit, tested state-machine transitions.

---

## Suggested Execution Order

1. Domain/state payload adjustments.
2. Image-loading service.
3. ViewModel API + transition path update.
4. File chooser integration.
5. UI image rendering + footer metadata.
6. Tests.
7. Verification checklist.
