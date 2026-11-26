UPDATE todos AS t
JOIN (
    SELECT '京都の紅葉スポットを調べる' AS title, 8000 AS budget_amount, 6500 AS actual_amount UNION ALL
    SELECT '箱根の日帰り旅行を計画する', 18000, 16000 UNION ALL
    SELECT '鎌倉散策のルートを決める', 6000, 5200 UNION ALL
    SELECT '札幌旅行の航空券を比較する', 45000, 42000 UNION ALL
    SELECT '近場の温泉宿を予約する', 25000, 28000 UNION ALL
    SELECT '新しいカフェを開拓する', 3000, 2800 UNION ALL
    SELECT '秋の味覚のコースを楽しむ', 12000, 11000 UNION ALL
    SELECT '友人とランチの店を決める', 5000, 5600 UNION ALL
    SELECT '冬物のコートを探す', 25000, 22000 UNION ALL
    SELECT '旅行用のモバイルバッテリーを買う', 8000, 7500 UNION ALL
    SELECT '陶芸の一日体験を予約する', 6000, 6000 UNION ALL
    SELECT 'クリスマスマーケットに行く', 5000, 4800 UNION ALL
    SELECT 'スキー初心者レッスンに申し込む', 15000, 18000 UNION ALL
    SELECT '家族でイルミネーションを見る', 3000, 2500 UNION ALL
    SELECT 'パン作り教室に参加する', 7000, 7000 UNION ALL
    SELECT '年末旅行の行き先を決める', 60000, 58000 UNION ALL
    SELECT 'おせち料理を予約する', 30000, 32000 UNION ALL
    SELECT '手帳と新年の文具を選ぶ', 5000, 4500 UNION ALL
    SELECT '来年の旅行予算を立てる', 0, 0 UNION ALL
    SELECT '今年読んだ本を振り返る', 2000, 1800 UNION ALL
    SELECT '奈良公園で鹿と紅葉を楽しむ', 9000, 7500 UNION ALL
    SELECT '評判のうどん店を訪ねる', 2500, 2200 UNION ALL
    SELECT '冬用の手袋を選ぶ', 6000, 5500 UNION ALL
    SELECT 'ガラス工芸の体験を予約する', 8000, 8000 UNION ALL
    SELECT '京都の寺社を巡る計画を立てる', 10000, 9200 UNION ALL
    SELECT '季節のパフェを食べに行く', 2500, 2800 UNION ALL
    SELECT '写真の構図について学ぶ', 4000, 3500 UNION ALL
    SELECT '防寒用のインナーを買う', 7000, 6500 UNION ALL
    SELECT '美術館の企画展を見に行く', 3500, 3000 UNION ALL
    SELECT '年末の帰省ルートを調べる', 30000, 28000
) AS sample_amounts ON t.title = sample_amounts.title
SET t.budget_amount = sample_amounts.budget_amount,
    t.actual_amount = sample_amounts.actual_amount;
