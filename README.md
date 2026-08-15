# Java Unicode Character Limit Debug Lab

Javaの表示名バリデーションで、絵文字を含む文字列を実際より長いものとして拒否する不具合を再現し、テストから修正までを追うための最小教材です。

## 扱う契約

表示名の上限を3文字とし、`A😀B`を受け入れて登録します。この教材では、利用者向けの「文字」をUnicodeコードポイントとして扱います。

| 入力 | コードポイント数 | UTF-16コード単位数 | 期待する結果 |
| --- | ---: | ---: | --- |
| `ABC` | 3 | 3 | 受理して登録する |
| `A😀B` | 3 | 4 | 受理して登録する |
| `A😀BC` | 4 | 5 | 拒否して登録しない |

バグ状態では`String.length()`でUTF-16コード単位数を数えるため、`A😀B`を4文字として拒否します。修正後は`String.codePointCount(0, displayName.length())`を使い、補助文字を1コードポイントとして数えます。

## 前提条件

Java 21以上が必要です。外部ライブラリやビルドツールは使用せず、JDK標準の`javac`と`java`だけでテストを実行します。

## バグを再現する

バグ状態のコミットは`8610ca6`です。

```bash
git clone https://github.com/tonbiattack/java-unicode-character-limit-debug-lab.git
cd java-unicode-character-limit-debug-lab

git checkout 8610ca6
./scripts/test.sh
```

`A😀B`について、受理結果、測定値、登録後状態の検証が失敗します。詳細は[デバッグ記録](./docs/debugging-record.md)を参照してください。

## 修正を確認する

`main`へ戻して同じテストを実行します。

```bash
git checkout main
./scripts/test.sh
```

修正済みのテストは次を同時に検証します。

- 3コードポイントの`A😀B`が受理されること。
- 判定結果の測定値が3になること。
- 受理した表示名がレジストリの最終状態に登録されること。
- 4コードポイントの`A😀BC`が拒否され、登録されないこと。

## 文書

| 文書 | 内容 |
| --- | --- |
| [docs/debugging-record.md](./docs/debugging-record.md) | 入力、失敗出力、観測、原因、修正、回帰確認、制約を記録します。 |
| [docs/novelty-report.md](./docs/novelty-report.md) | 既存のJava記事・リポジトリとの重複を調査した記録です。 |

## 制約

`codePointCount`はUnicodeコードポイント数を数えます。結合文字列や絵文字のZWJシーケンスまで「画面上で1文字」と数えるグラフェムクラスタ単位の上限は、この教材の対象外です。その要件が必要な場合は、UI仕様とUnicodeテキスト分割の方針を別途決めてください。
