# Java Unicode文字数題材の重複調査

## 調査対象

| 項目 | 内容 |
| --- | --- |
| 新規題材 | Javaの文字数制限で絵文字を誤って拒否する |
| 契約 | `A😀B`を3コードポイントとして受け入れ、登録する。 |
| バグ状態 | `String.length()`が返す4 UTF-16コード単位を文字数として扱い、拒否する。 |
| 最小修正 | `String.codePointCount(0, displayName.length())`を使う。 |
| カタログ | `/home/ubuntu/repository-catalog/data/repositories.json` |
| カタログ更新・検証 | 2026-08-15 UTCに`refresh_catalog.py`と`validate_catalog.py`を実行し、437件・学習用候補87件で検証成功。 |

## 自動スクリーニング

次の条件で`check_catalog_overlap.py`を実行しました。

```bash
python3 check_catalog_overlap.py \
  --language Java \
  --title 'Javaの文字数制限で絵文字を誤って拒否する' \
  --contract 'A😀Bを3文字として受け入れるべきだが、String.lengthを使うバグ状態では4として拒否する' \
  --keywords 'Java,Unicode,codePointCount,String,length,UTF-16,emoji,validation'
```

カタログ上の名称・説明・タグからは、機械的な近接候補は抽出されませんでした。さらに記事リポジトリとGitHubコードを`codePoint`、`Unicode`、`UTF-16`、`サロゲート`、絵文字と文字数の組合せで検索しました。JavaのUnicode文字数契約を直接扱う既存記事・Javaリポジトリは見つかりませんでした。

## 手動比較

自動スクリーニングが候補なしでも新規性を断定しないため、近いJava教材・記事を手動で比較しました。

| 比較対象 | 既存題材の原因・境界・契約・修正 | 今回の差分 | 判定 |
| --- | --- | --- | --- |
| `language-agnostic-debugging-lab` | null、off-by-one、整数除算、資源解放、競合、タイムゾーンをCLIシナリオとして扱う。Unicodeの文字数計測は扱わない。 | 直接原因はUTF-16コード単位とコードポイントの混同。実境界は`DisplayNameRegistry#register`、契約は表示名の受理と登録後集合、修正は`codePointCount`への変更である。 | 重複なし |
| Qiita下書き「JavaでSetの重複除去が効かない：equalsとhashCodeを値オブジェクトに実装する」 | `HashSet`で値オブジェクトの等価性を定義しないことが原因。重複要素が残らない契約を検証し、`equals`と`hashCode`を実装する。 | 原因は文字列の単位、実境界は表示名の上限判定、観測契約は受理結果・計測値・登録後状態、修正は文字数APIの選択である。 | 重複なし |
| `spring-localdatetime-timezone-debug-lab` | Spring Bootで日時とタイムゾーンを取り違えるフレームワーク境界の題材。 | Spring非依存のJava標準ライブラリだけを使い、UTF-16とUnicodeコードポイントの差を扱う。 | 重複なし |

四比較軸で、既存題材と同じ直接原因、実境界、観測契約、最小修正を持つものは確認されませんでした。

## 作成可否

- [x] Repository Catalogを更新・検証した。
- [x] 自動スクリーニングで近接候補なしを確認した。
- [x] 近いJava教材・記事について、原因、実境界、観測契約、最小修正を比較した。
- [x] 同じ失敗を別名で再実装していない。
- [x] フレームワーク非依存の再現、失敗テスト、最小修正、回帰、分離したGit履歴を計画した。
