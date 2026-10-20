# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Describe a task's completion or due-date status using a D-day label. Count the total calendar days between today and the due date, including across months, years, and leap days.

**Pre-conditions**
- `today` is a valid non-null `LocalDate`; `dueDate` is either a valid `LocalDate` or null.
- Dates are calendar dates, with no time-of-day or timezone conversion. ISO date literals below denote `LocalDate` values.

**Post-conditions**
- Success cases
  - Completed tasks show `"CLEAR"` regardless of the due date. `[Unit]`
    - Tip: Given dueDate=2026-10-20, today=2026-10-21, isCompleted=true / When dueDateLabel is called / Then return "CLEAR".
  - Completion takes precedence even without a due date. `[Unit]`
    - Tip: Given dueDate=null, today=2026-10-21, isCompleted=true / When dueDateLabel is called / Then return "CLEAR".
  - Active tasks without a due date have an empty label. `[Unit]`
    - Tip: Given dueDate=null, today=2026-10-21, isCompleted=false / When dueDateLabel is called / Then return "".
  - Active tasks due today show `"D-Day"`. `[Unit]`
    - Tip: Given dueDate=2026-10-21, today=2026-10-21, isCompleted=false / When dueDateLabel is called / Then return "D-Day".
  - Future dates within the same month show `"D-N"`, where N is the number of days left, with no leading zeros. `[Unit]`
    - Tip: Given dueDate=2026-10-24, today=2026-10-21, isCompleted=false / When dueDateLabel is called / Then return "D-3".
  - Past dates within the same month show `"D+N"`, where N is the number of overdue days, with no leading zeros. `[Unit]`
    - Tip: Given dueDate=2026-10-18, today=2026-10-21, isCompleted=false / When dueDateLabel is called / Then return "D+3".
  - Future dates more than one month away still count the total number of calendar days. `[Unit]`
    - Tip: Given dueDate=2026-12-21, today=2026-10-21, isCompleted=false / When dueDateLabel is called / Then return "D-61".
  - Past dates more than one month ago still count the total number of overdue calendar days. `[Unit]`
    - Tip: Given dueDate=2026-10-21, today=2026-12-21, isCompleted=false / When dueDateLabel is called / Then return "D+61".
- Error cases
  - None. All inputs satisfying the pre-conditions are valid, including null due dates and past dates.
- Edge cases
  - Check the overdue/today/future boundary one day on either side of today. `[Unit]`
    - Tip: Given today=2026-10-21, isCompleted=false, and dueDate values 2026-10-20, 2026-10-21, 2026-10-22 / When dueDateLabel is called for each / Then return "D+1", "D-Day", "D-1", respectively.
  - Completion overrides each side of the today boundary. `[Unit]`
    - Tip: Given today=2026-10-21, isCompleted=true, and dueDate values 2026-10-20, 2026-10-21, 2026-10-22 / When dueDateLabel is called for each / Then return "CLEAR", "CLEAR", "CLEAR", respectively.
  - Count continuously immediately before, at, and after a month starts. `[Unit]`
    - Tip: Given today=2026-01-30, isCompleted=false, and dueDate values 2026-01-31, 2026-02-01, 2026-02-02 / When dueDateLabel is called for each / Then return "D-1", "D-2", "D-3", respectively.
  - Overdue counts also cross a month start continuously. `[Unit]`
    - Tip: Given dueDate=2026-01-30, isCompleted=false, and today values 2026-01-31, 2026-02-01, 2026-02-02 / When dueDateLabel is called for each / Then return "D+1", "D+2", "D+3", respectively.
  - A whole month is not zero days; check immediately below, at, and above a whole month. `[Unit]`
    - Tip: Given today=2026-01-15, isCompleted=false, and dueDate values 2026-02-14, 2026-02-15, 2026-02-16 / When dueDateLabel is called for each / Then return "D-30", "D-31", "D-32", respectively.
  - Whole-month overdue counts use total days as well. `[Unit]`
    - Tip: Given dueDate=2026-01-15, isCompleted=false, and today values 2026-02-14, 2026-02-15, 2026-02-16 / When dueDateLabel is called for each / Then return "D+30", "D+31", "D+32", respectively.
  - A whole year is not zero days; check immediately below, at, and above a whole year. `[Unit]`
    - Tip: Given today=2026-10-21, isCompleted=false, and dueDate values 2027-10-20, 2027-10-21, 2027-10-22 / When dueDateLabel is called for each / Then return "D-364", "D-365", "D-366", respectively.
  - Whole-year overdue counts use total days as well. `[Unit]`
    - Tip: Given dueDate=2026-10-21, isCompleted=false, and today values 2027-10-20, 2027-10-21, 2027-10-22 / When dueDateLabel is called for each / Then return "D+364", "D+365", "D+366", respectively.
  - Count continuously immediately before, at, and after a year starts. `[Unit]`
    - Tip: Given today=2026-12-30, isCompleted=false, and dueDate values 2026-12-31, 2027-01-01, 2027-01-02 / When dueDateLabel is called for each / Then return "D-1", "D-2", "D-3", respectively.
  - Overdue counts also cross a year start continuously. `[Unit]`
    - Tip: Given dueDate=2026-12-30, isCompleted=false, and today values 2026-12-31, 2027-01-01, 2027-01-02 / When dueDateLabel is called for each / Then return "D+1", "D+2", "D+3", respectively.
  - Include February 29 immediately before, at, and after the leap day. `[Unit]`
    - Tip: Given today=2028-02-27, isCompleted=false, and dueDate values 2028-02-28, 2028-02-29, 2028-03-01 / When dueDateLabel is called for each / Then return "D-1", "D-2", "D-3", respectively.
  - Overdue counts include the leap day too. `[Unit]`
    - Tip: Given dueDate=2028-02-27, isCompleted=false, and today values 2028-02-28, 2028-02-29, 2028-03-01 / When dueDateLabel is called for each / Then return "D+1", "D+2", "D+3", respectively.
  - A non-leap February proceeds directly to March. `[Unit]`
    - Tip: Given today=2027-02-27, isCompleted=false, and dueDate values 2027-02-28, 2027-03-01, 2027-03-02 / When dueDateLabel is called for each / Then return "D-1", "D-2", "D-3", respectively.

**Undefined behavior** (optional)
- None within the pre-conditions. Constructing an invalid calendar date is outside this function's contract.