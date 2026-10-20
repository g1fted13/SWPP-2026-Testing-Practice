# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Make a task row show its current D-day or completion label. Clear and hide the label when no task or no active due date is present, including when a row is reused.

**Pre-conditions**
- `textView` is a valid Android `TextView`, updated on the UI thread; `task` may be null.
- The function reads today's date from the device clock (`LocalDate.now()`), and a test cannot change that clock. In every Tip, `today` means `LocalDate.now()` read by the test right before the call, and due dates are written relative to it (for example, `today+1` is `LocalDate.now().plusDays(1)`).
- In every Tip, a non-null task has title="Task", description="Description", id="task-1", and the explicitly specified dueDate and isCompleted values. Unless overridden, the TextView initially has text="stale" and visibility=View.VISIBLE (0).

**Post-conditions**
- Success cases
  - A completed task displays `"CLEAR"` and is visible even if overdue. `[Integration]`
    - Tip: Given task.dueDate=today-1, task.isCompleted=true / When setDueDateLabel is called / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0).
  - A completed task without a due date still displays `"CLEAR"`. `[Integration]`
    - Tip: Given task.dueDate=null, task.isCompleted=true / When setDueDateLabel is called / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0).
  - An active task without a due date clears the text and removes the label's layout space. `[Integration]`
    - Tip: Given task.dueDate=null, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="" and visibility=View.GONE (8).
  - A null task clears the text and removes the label's layout space. `[Integration]`
    - Tip: Given task=null / When setDueDateLabel is called / Then textView.text.toString()="" and visibility=View.GONE (8).
  - A task due today displays `"D-Day"` and is visible. `[Integration]`
    - Tip: Given task.dueDate=today, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D-Day" and visibility=View.VISIBLE (0).
  - A future due date displays the total remaining calendar days and is visible. `[Integration]`
    - Tip: Given task.dueDate=today+61, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D-61" and visibility=View.VISIBLE (0).
  - An overdue task displays the total overdue calendar days and is visible. `[Integration]`
    - Tip: Given task.dueDate=today-61, task.isCompleted=false / When setDueDateLabel is called / Then textView.text.toString()="D+61" and visibility=View.VISIBLE (0).
- Error cases
  - None. All inputs satisfying the pre-conditions are valid, including a null task and null due dates.
- Edge cases
  - Show the correct label immediately before, on, and after today's due-date boundary. `[Integration]`
    - Tip: Given task.isCompleted=false and task.dueDate values today-1, today, today+1 / When setDueDateLabel is called for each / Then textView.text.toString() is "D+1", "D-Day", "D-1", respectively, and visibility=View.VISIBLE (0) for every call.
  - Completion overrides all three date categories. `[Integration]`
    - Tip: Given task.isCompleted=true and task.dueDate values today-1, today, today+1 / When setDueDateLabel is called for each / Then textView.text.toString()="CLEAR" and visibility=View.VISIBLE (0) for every call.
  - Rebinding across absent, present, and absent due dates must clear and restore the same view. `[Integration]`
    - Tip: Given task.isCompleted=false / When setDueDateLabel is called on the same TextView with task.dueDate=null, then today+1, then null / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("D-1", View.VISIBLE (0)), ("", View.GONE (8)).
  - Rebinding across null, non-null, and null tasks must clear and restore the same view. `[Integration]`
    - Tip: Given a task with dueDate=today and isCompleted=false / When setDueDateLabel is called on the same TextView with null, then that task, then null / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("D-Day", View.VISIBLE (0)), ("", View.GONE (8)).
  - Toggling completion with no due date switches between hidden and visible states without stale text. `[Integration]`
    - Tip: Given task.dueDate=null / When setDueDateLabel is called on the same TextView with task.isCompleted=false, then true, then false / Then the successive (textView.text.toString(), visibility) values are ("", View.GONE (8)), ("CLEAR", View.VISIBLE (0)), ("", View.GONE (8)).
  - Toggling completion with a due date switches between the D-day label and `"CLEAR"`. `[Integration]`
    - Tip: Given task.dueDate=today+1 / When setDueDateLabel is called on the same TextView with task.isCompleted=false, then true, then false / Then the successive (textView.text.toString(), visibility) values are ("D-1", View.VISIBLE (0)), ("CLEAR", View.VISIBLE (0)), ("D-1", View.VISIBLE (0)).
  - A nonempty label restores visibility from either hidden visibility value. `[Integration]`
    - Tip: Given task.dueDate=today+1, task.isCompleted=false, and TextViews initially with text="stale" and visibility values View.VISIBLE (0), View.INVISIBLE (4), View.GONE (8) / When setDueDateLabel is called for each / Then textView.text.toString()="D-1" and visibility=View.VISIBLE (0) for all three.

**Undefined behavior** (optional)
- How the label changes when the device date moves between two bindings (for example, across local midnight) is not specified: the function reads the device clock directly, so a test cannot set "today" without changing production code.
- A test run in which local midnight passes between reading `LocalDate.now()` and calling the function is out of scope.