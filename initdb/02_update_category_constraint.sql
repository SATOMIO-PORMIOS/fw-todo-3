-- Existing MySQL volumes do not rerun 01_create_table.sql.
-- Run this migration once against an existing database before deploying the new categories.
ALTER TABLE todos DROP CHECK chk_todos_category;

-- Convert the five former categories to the new category set.
UPDATE todos
SET category = CASE category
    WHEN 'デザイン' THEN '体験'
    WHEN 'マーケティング' THEN '買い物'
    WHEN 'プログラミング' THEN '学習'
    WHEN '資格' THEN '学習'
    WHEN '就職活動' THEN '学習'
    ELSE '学習'
END;

ALTER TABLE todos
    ADD CONSTRAINT chk_todos_category CHECK (category IN (
        '旅行', 'グルメ', '買い物', '体験', '学習'
    ));
