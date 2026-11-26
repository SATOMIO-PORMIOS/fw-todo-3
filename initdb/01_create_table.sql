CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    budget_amount BIGINT NULL,
    actual_amount BIGINT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_todos_category CHECK (category IN (
        '旅行', 'グルメ', '買い物', '体験', '学習'
    )),
    CONSTRAINT chk_todos_priority CHECK (priority IN (1, 2, 3)),
    CONSTRAINT chk_todos_completed CHECK (completed IN (0, 1)),
    CONSTRAINT chk_todos_budget_amount CHECK (budget_amount IS NULL OR budget_amount >= 0),
    CONSTRAINT chk_todos_actual_amount CHECK (actual_amount IS NULL OR actual_amount >= 0)
);
