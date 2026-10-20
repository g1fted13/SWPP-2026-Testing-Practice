# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Make a task row show its current D-day or completion label. Clear and hide the label when no task or no active due date is present, including when a row is reused.

**Pre-conditions**
- `textView` is a valid Android `TextView`, updated on the UI thread; `task` may be null.
- Today is the device's current local calendar date at the call. Integration scenarios must control that date; ISO date literals below denote `LocalDate` values.
- In every Tip, a non-null task has title="Task", description="Description", id="task-1", and the explicitly specified dueDate and isCompleted values. Unless overridden, the TextView initially has text="stale" and visibility=View.VISIBLE (0).

**Post-conditions**
- Success cases
  - A completed task displays `"CLEAR"` and is visible even if overdue. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=2026-10-20, task.isCompleted=true / When setDueDateLabel is called / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0).
  - A completed task without a due date still displays `"CLEAR"`. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=null, task.isCompleted=true / When setDueDateLabel is called / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0).
  - An active task without a due date clears the text and removes the label's layout space. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=null, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="" and visibility=View.GONE (8).
  - A null task clears the text and removes the label's layout space. [Integration]
    - Tip: Given today=2026-10-21 and task=null / When setDueDateLabel is called / Then textView.text.toString()="" and visibility=View.GONE (8).
  - A task due today displays `"D-Day"` and is visible. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=2026-10-21, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D-Day" and visibility=View.VISIBLE (0).
  - A future due date displays the total remaining calendar days and is visible. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=2026-12-21, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D-61" and visibility=View.VISIBLE (0).
  - An overdue task displays the total overdue calendar days and is visible. [Integration]
    - Tip: Given today=2026-12-21, task.dueDate=2026-10-21, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D+61" and visibility=View.VISIBLE (0).
- Error cases
  - None. All inputs satisfying the pre-conditions are valid, including a null task and null due dates.
- Edge cases
  - Show the correct label immediately before, on, and after today's due-date boundary. [Integration]
    - Tip: Given today=2026-10-21, task.isCompleted=false, and task.dueDate values 2026-10-20, 2026-10-21, 2026-10-22 / When setDueDateLabel is called for each / Then textView.text.toString() is "D+1", "D-Day", "D-1", respectively, and visibility=View.VISIBLE (0) for every call.
  - Completion overrides all three date categories. [Integration]
    - Tip: Given today=2026-10-21, task.isCompleted=true, and task.dueDate values 2026-10-20, 2026-10-21, 2026-10-22 / When setDueDateLabel is called for each / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0) for every call.
  - Rebinding across absent, present, and absent due dates must clear and restore the same view. [Integration]
    - Tip: Given today=2026-10-21 and task.isCompleted=false / When setDueDateLabel is called on the same TextView with task.dueDate=null, then 2026-10-22, then null / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("D-1", View.VISIBLE (0)), ("", View.GONE (8)).
  - Rebinding across null, non-null, and null tasks must clear and restore the same view. [Integration]
    - Tip: Given today=2026-10-21 and a task with dueDate=2026-10-21 and isCompleted=false / When setDueDateLabel is called on the same TextView with null, then that task, then null / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("D-Day", View.VISIBLE (0)), ("", View.GONE (8)).
  - Toggling completion with no due date switches between hidden and visible states without stale text. [Integration]
    - Tip: Given today=2026-10-21 and task.dueDate=null / When setDueDateLabel is called on the same TextView with task.isCompleted=false, then true, then false / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("CLEAR", View.VISIBLE (0)), ("", View.GONE (8)).
  - A nonempty label restores visibility from either hidden visibility value. [Integration]
    - Tip: Given today=2026-10-21, task.dueDate=2026-10-22, task.isCompleted=false, and TextViews initially with text="stale" and visibility values View.VISIBLE (0), View.INVISIBLE (4), View.GONE (8) / When setDueDateLabel is called for each / Then textView.text.toString()="D-1" and visibility=View.VISIBLE (0) for all three.
  - Crossing local midnight updates today's label on the next binding call. [Integration]
    - Tip: Given task.dueDate=2027-01-01 and task.isCompleted=false / When setDueDateLabel is called with the device local date set successively to 2026-12-31, 2027-01-01, 2027-01-02 / Then textView.text.toString() is "D-1", "D-Day", "D+1", respectively, and visibility=View.VISIBLE (0) for every call.

**Undefined behavior** (optional)
- A call racing a device date/timezone change is outside the deterministic scenarios above; hold the device's local date stable during each call.
