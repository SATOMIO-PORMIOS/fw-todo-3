ALTER TABLE todos
    ADD CONSTRAINT chk_todos_budget_amount CHECK (budget_amount IS NULL OR budget_amount >= 0),
    ADD CONSTRAINT chk_todos_actual_amount CHECK (actual_amount IS NULL OR actual_amount >= 0);
