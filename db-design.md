# ToDoアプリ DB設計書

この文書は、`handout_app_spec.md`と`requirements.md`をもとにしたデータベース（データの保存場所）の設計です。画面の設計は別の文書で扱います。

## 1. 保存する場所

- 使用するDBはMySQL。
- 使用するテーブル（データを行として保存する表）は`todos`の1つだけ。
- やること1件を`todos`の1行として保存する。
- `id`を主キー（各行を重複なく見分ける番号）にする。

## 2. カラム一覧

カラムは表の列を指します。`NULL`は「値が入っていない」状態です。

| カラム | 型 | NULL | 初期値・自動設定 | 用途 |
|---|---|---|---|---|
| `id` | `BIGINT` | 不可 | 1ずつ自動採番 | 1件を見分ける番号。主キー。 |
| `title` | `VARCHAR(255)` | 不可 | なし | やることの名前。 |
| `detail` | `VARCHAR(255)` | 可 | なし | メモ。 |
| `category` | `VARCHAR(255)` | 不可 | なし | 5つから選ぶジャンル。 |
| `priority` | `INT` | 不可 | `2`（中） | 優先度。高＝1、中＝2、低＝3。 |
| `due_date` | `DATE` | 可 | なし | 期限。 |
| `completed` | `BOOLEAN` | 不可 | `FALSE`（未完了） | 完了状態。完了＝1、未完了＝0。 |
| `created_at` | `DATETIME` | 不可 | MySQLが登録時刻を設定 | 登録した日時。 |
| `updated_at` | `DATETIME` | 不可 | MySQLが登録時・更新時に設定 | 最後に直した日時。 |

`id`、`created_at`、`updated_at`は入力欄に出さず、利用者に入力させません。

## 3. 値の決まり

- `category`は「デザイン」「マーケティング」「プログラミング」「資格」「就職活動」のどれか1つです。
- `priority`は`1`（高）、`2`（中）、`3`（低）のどれかです。
- `completed`は`0`（未完了）または`1`（完了）です。
- `title`と`category`は必須、`detail`と`due_date`は省略できます。登録・編集画面では`priority`も必ず選びます。
- `title`の空文字・空白だけの入力と、`title`・`detail`の255文字超過は、保存前の入力確認で止めます。`NOT NULL`は空白だけの文字列までは防がないためです。

## 4. CREATE TABLE文（DDL）

DDLは、テーブルを作るためのSQL文です。次の文は設計として記載するもので、この文書作成時には実行しません。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_todos_category CHECK (category IN (
        'デザイン', 'マーケティング', 'プログラミング', '資格', '就職活動'
    )),
    CONSTRAINT chk_todos_priority CHECK (priority IN (1, 2, 3)),
    CONSTRAINT chk_todos_completed CHECK (completed IN (0, 1))
);
```

`CHECK`は、決められた値以外を保存しないための条件です。ジャンル・優先度・完了状態の選択肢を、元文書の決まりに合わせています。

## 5. 要件とカラムの対応

| 要件・画面での使い道 | 対応するカラム | 元文書の箇所 |
|---|---|---|
| 1件を見分け、編集・削除する | `id` | 叩き台 2、3、4／要件定義 2、3、4 |
| やることを登録・表示・名前で部分検索する | `title` | 叩き台 1、2、4、5-1、5-4／要件定義 2、3、5、6 |
| 任意のメモを登録・編集する | `detail` | 叩き台 2、4、5-1／要件定義 3、5、6 |
| 5つのジャンルから選び、完全一致で絞り込む | `category` | 叩き台 2、4、5-4／要件定義 3、5、6 |
| 高・中・低を選び、一覧に表示する | `priority` | 叩き台 2、4、5-1／要件定義 3、5、6 |
| 任意の期限を登録し、期限順に並べる | `due_date` | 叩き台 2、4、5-4／要件定義 2、3、5、6 |
| 編集時に完了状態を変え、一覧に表示する | `completed` | 叩き台 2、4／要件定義 3、5 |
| 登録した日時を自動で残す | `created_at` | 叩き台 2／要件定義 3 |
| 最後に直した日時を自動で残す | `updated_at` | 叩き台 2／要件定義 3 |

## 6. 設計上の補足

- `CHECK`で値の範囲を守る方法は、この設計書で決めた実装方法です。選択肢そのものは元文書に記載されています。
- 検索用の追加の索引（検索を速くする仕組み）は、元文書で指定されていないため設けません。主キーの`id`にはMySQLが索引を作ります。
- 別のテーブル、外部キー（別の表との結び付き）、初期データは設けません。
