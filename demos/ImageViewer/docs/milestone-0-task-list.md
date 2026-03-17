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

- [ ] Create `ImageViewerUiState` with placeholder-first fields:
  - app title / static labels (optional)
  - `selectedProcessingMode` (default placeholder value)
  - `renderState` (default `Idle`)
  - `hasImageLoaded` (false for now) or equivalent placeholder flags
  - placeholder filter-control state collection (can be empty)
- [ ] Ensure state type is immutable from UI perspective.
- [ ] Add initial state factory/default constructor strategy.

Acceptance criteria:

- UI can render entirely from one state object.
- No direct mutable UI state inside leaf composables.

---

## 4. Define Event Contract (State Machine Inputs)

- [ ] Create `ImageViewerEvent` sealed type with Milestone-0-needed events:
  - `OpenImageClicked`
  - `SaveImageClicked`
  - `ResetClicked`
  - `ProcessingModeChanged(mode)`
  - placeholder filter interaction events (optional stubs)
- [ ] Keep event names intention-revealing and aligned with user actions.
- [ ] Do not implement file/pipeline logic yet; only event wiring contract.

Acceptance criteria:

- UI interactions dispatch typed events instead of mutating state inline.
- Event list is sufficient for shell interactivity and future extension.

---

## 5. Introduce ViewModel Stub and Wiring

- [ ] Create `ImageViewerViewModel` with:
  - exposed `uiState`
  - `onEvent(event: ImageViewerEvent)` entry point
- [ ] Implement minimal reducer behavior for shell-level interactions only (e.g., processing mode selection).
- [ ] Keep side effects as TODO placeholders for future milestones.
- [ ] Ensure ViewModel acts as single orchestrator between UI and future processing layer.

Acceptance criteria:

- UI reads state from ViewModel and sends events to ViewModel.
- No business logic hardcoded in composables.

---

## 6. Build Base Shell Layout

- [ ] Compose top bar with disabled or placeholder actions:
  - `Open`
  - `Save As (PNG)`
  - `Reset`
- [ ] Compose main content split:
  - left preview area with placeholders for `Original` and `Processed`
  - right control area placeholder for processing mode and filter controls
- [ ] Compose status/footer area:
  - render state indicator
  - placeholder file metadata text
- [ ] Ensure layout scales reasonably for desktop window resize.

Acceptance criteria:

- All planned shell regions are visible and clearly separated.
- Buttons/controls are wired to event dispatch (even if behavior is stubbed).

---

## 7. Processing Mode Selector Placeholder

- [ ] Add UI control for selecting `Threads` or `Coroutines`.
- [ ] Bind selected value to hoisted state.
- [ ] Update state through ViewModel event handling.

Acceptance criteria:

- User can switch mode in UI and selection persists in state.
- No pipeline implementation required yet.

---

## 8. Placeholder Error/Status UX

- [ ] Add non-blocking status presentation area for:
  - `Idle`
  - `Rendering` (placeholder only)
  - `Error(message)` (placeholder-trigger path optional)
- [ ] Ensure message area does not break layout when empty.

Acceptance criteria:

- Status rendering path exists and is driven by state.
- Future milestones can hook real errors without redesigning UI.

---

## 9. Minimal Test Setup for Milestone 0

- [ ] Replace template test with at least one meaningful state-oriented test.
- [ ] Add ViewModel test for a simple event transition:
  - example: `ProcessingModeChanged` updates state.
- [ ] Keep tests lightweight but aligned with MVVM/state-machine approach.

Acceptance criteria:

- Test suite includes at least one non-template test tied to new architecture.

---

## 10. Final Verification Checklist

- [ ] `./gradlew :composeApp:run` launches app shell successfully.
- [ ] `./gradlew :composeApp:test` passes with updated tests.
- [ ] No template artifacts remain in source or UI.
- [ ] No TODO blockers that prevent starting Milestone 1.

Exit condition for Milestone 0:

- Application starts with a functional shell, state-hoisted MVVM wiring is in place, and codebase is ready for image-loading implementation.

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
