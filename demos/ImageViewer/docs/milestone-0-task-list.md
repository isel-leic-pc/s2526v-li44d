# Milestone 0 Detailed Task List

## Purpose

Milestone 0 sets up a clean application shell aligned with MVVM and state hoisting, replacing template code and preparing the project for Milestone 1 (image loading + state machine behavior).

This milestone does not implement image processing or file I/O yet.

---

## Deliverables

- Template UI removed.
- App shell screen rendered from composables focused on layout and placeholders.
- Hoisted UI state model defined and wired to UI.
- Event contract stubbed and connected (without full business logic).
- App runs successfully with placeholder content and no regression in startup.

---

## Task Breakdown

## 1. Cleanup Template Artifacts

- [x] Remove greeting/template concepts from UI (`Greeting`, old button-toggle demo behavior).
- [x] Replace current `App()` content with a dedicated app-shell entry composable.
- [x] Keep desktop entry point (`main.kt`) minimal and pointed to new shell.
- [x] Externalize window title into Compose resources.
- [x] Ensure compile passes after removing template-specific references.

Acceptance criteria:

- No template text/images remain in the running UI.
- Application still launches from `:composeApp:run`.
- Window title comes from resources (not hardcoded in the source code).

Status:

- Completed on 2026-03-17.
- Build verification succeeded with:
  - `export JAVA_HOME=$(/usr/libexec/java_home -v 21) && ./gradlew :composeApp:compileKotlinJvm`
- Minor adjustment applied on 2026-03-17:
  - Window title set to `Image Viewer demo` from `composeResources/values/strings.xml`.

---

## 2. Create Initial Package Skeleton

- [x] Create/confirm package folders:
  - `ui/`
  - `ui/components/`
  - `viewmodel/`
  - `domain/`
- [x] Move top-level UI entry into `ui/` (for example: `AppScreen`).
- [x] Create placeholder ViewModel file:
  - `viewmodel/ImageViewerViewModel.kt`

Acceptance criteria:

- Project tree reflects target package architecture for upcoming milestones.
- Names are consistent with plan to avoid refactor churn.

Status:

- Completed on 2026-03-17.
- Build verification:
  - `export JAVA_HOME=$(/usr/libexec/java_home -v 21) && ./gradlew :composeApp:compileKotlinJvm`

---

## 3. Define Hoisted UI State Model

- [x] Expose `ImageViewerScreenState` directly from `ImageViewerViewModel` as snapshot state.
- [x] Keep transition logic driven by `ImageViewerScreenState` state-machine operations.
- [x] Postpone `ImageViewerUiState` wrapper until additional UI-specific properties are needed.
- [x] Postpone non-essential UI fields (`selectedProcessingMode`, filter controls, render metrics) to later steps.

Acceptance criteria:

- UI can render from a single hoisted state object (`ImageViewerScreenState` for now).
- State remains immutable from the UI perspective.
- No direct mutable UI state inside leaf composables.

Status:

- Completed on 2026-03-18 with direct `ImageViewerScreenState` exposure in `ImageViewerViewModel`.
- Verification:
  - `export JAVA_HOME=$(/usr/libexec/java_home -v 21) && ./gradlew :composeApp:jvmTest`

---

## 4. Define Event Contract (State Machine Inputs)

- [x] Define operation-based state-machine inputs on `ImageViewerViewModel`:
  - `requestLoadImage(imageName)`
  - `dismissError()`
  - `reset()`
- [x] Keep transition API intention-revealing and aligned with user actions.
- [x] Implement only image-loading state-machine flow for now.

Acceptance criteria:

- UI can invoke ViewModel operations instead of mutating state inline.
- Operation set is sufficient for current shell image-load flow and extension.

Status:

- Completed on 2026-03-18 with operation-based API (no `ImageViewerEvent` type in this iteration).

---

## 5. Introduce ViewModel Stub and Wiring

- [x] Create `ImageViewerViewModel` contract with exposed screen state:
  - `var state by mutableStateOf(...)`
- [x] Expose operation-based transition methods (`requestLoadImage`, `dismissError`, `reset`).
- [x] Implement asynchronous load transition path using dedicated threads.
- [x] Provide distinct per-mode ViewModel implementations with same public API/state machine.
- [x] Keep non-load concerns for later milestones.

Acceptance criteria:

- UI reads state from ViewModel and invokes transition operations on ViewModel.
- No business logic hardcoded in composables.

Status:

- Completed on 2026-03-19 with:
  - shared `ImageViewerViewModel` contract
  - `ThreadsImageViewerViewModel`
  - `CoroutinesImageViewerViewModel`
- Pending in later milestones: filter/save transitions and cancellation model.

---

## 6. Build Base Shell Layout

- [x] Compose top bar with disabled or placeholder actions:
  - `Open`
  - `Save As (PNG)`
  - `Reset`
- [x] Compose main content split:
  - left preview area with placeholders for `Original` and `Processed`
  - right control area placeholder for processing mode and filter controls
- [x] Compose status/footer area:
  - render state indicator
  - placeholder file metadata text
- [x] Ensure layout scales reasonably for desktop window resize.

Acceptance criteria:

- All planned shell regions are visible and clearly separated.
- Buttons/controls are wired to event dispatch (even if behavior is stubbed).

Status:

- Completed on 2026-03-18.
- `Open`, `Reset`, and `Dismiss Error` are wired to ViewModel operations.
- `Save As (PNG)` is intentionally placeholder/disabled until export flow milestone.

---

## 7. Processing Mode Selector Placeholder

- [x] Add UI control for selecting `Threads` or `Coroutines`.
- [x] Bind selected value to hoisted state.
- [x] Update state through ViewModel event handling.

Acceptance criteria:

- User can switch mode in UI and selection persists in state.
- No pipeline implementation required yet.

Status:

- Completed on 2026-03-19.
- `ProcessingMode` selection is hoisted at app level.
- Main screen controls panel is wired to switch between mode-specific ViewModel instances.

---

## 8. Placeholder Error/Status UX

- [x] Add non-blocking status presentation area for:
  - `Idle`
  - `Rendering` (placeholder only)
  - `Error(message)` (placeholder-trigger path optional)
- [x] Ensure message area does not break layout when empty.

Acceptance criteria:

- Status rendering path exists and is driven by state.
- Future milestones can hook real errors without redesigning UI.

Status:

- Completed on 2026-03-19.
- Footer shows status + file metadata for all states and error details when present.
- Error UX is non-blocking and supports dismiss action.

---

## 9. Minimal Test Setup for Milestone 0

- [x] Replace template test with at least one meaningful state-oriented test.
- [x] Add ViewModel tests for state-machine transitions in the current load flow.
- [x] Keep tests lightweight, one behavior per test, following AAA.

Acceptance criteria:

- Test suite includes at least one non-template test tied to new architecture.

Status:

- Completed on 2026-03-18 with `ImageViewerViewModelTest` aligned to async `requestLoadImage`.
- Verification:
  - `export JAVA_HOME=$(/usr/libexec/java_home -v 21) && ./gradlew :composeApp:jvmTest`

---

## 10. Final Verification Checklist

- [x] `./gradlew :composeApp:run` launches app shell successfully.
- [x] `./gradlew :composeApp:jvmTest` passes with updated tests.
- [x] No template artifacts remain in source or UI.
- [x] No TODO blockers that prevent starting Milestone 1.

Exit condition for Milestone 0:

- Application starts with a functional shell, state-hoisted MVVM wiring is in place, and codebase is ready for image-loading implementation.

Status:

- Completed on 2026-03-19.
- Launch verification: `:composeApp:run` started successfully (task interrupted manually after startup).
- Test verification: `:composeApp:jvmTest` passed.

---

## Suggested Execution Order

1. Cleanup template artifacts.
2. Create package skeleton.
3. Define state + events.
4. Implement ViewModel stub.
5. Build shell layout and wire events.
6. Add processing mode selector + status area.
7. Update tests.
8. Run verification checklist.
