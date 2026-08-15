# 絵文字を含む表示名を誤って拒否する問題のデバッグ記録

## 対象の不具合

表示名の上限を3文字とする登録処理で、`A😀B`を受け入れて登録する契約を扱います。`A`、`😀`、`B`は3つのUnicodeコードポイントです。しかしバグ状態では`String.length()`を使ってUTF-16コード単位数を数えるため、`😀`を2単位として扱い、全体を4として拒否します。

| 項目 | 期待値 | バグ状態での実際値 |
| --- | --- | --- |
| `A😀B`の受理結果 | `true` | `false` |
| 判定に使う文字数 | `3`コードポイント | `4`コード単位 |
| 登録後状態 | `A😀B`を含む | 空集合 |
| `A😀BC`の測定値 | `4`コードポイント | `5`コード単位 |

## 再現条件

バグを含むコミットは`8610ca6`です。Java 21以上を用意して、次を実行します。

```bash
git checkout 8610ca6
./scripts/test.sh
```

実行すると、設定やコンパイルではなく、期待対実際の差分で失敗します。

```text
Exception in thread "main" java.lang.AssertionError: expected=true actual=false: 3コードポイントの表示名を受け入れる
expected=3 actual=4: コードポイント数を結果へ返す
expected=true actual=false: 受理した表示名を登録する
expected=[A😀B] actual=[]: 最終状態は受理した表示名だけを持つ
expected=4 actual=5: 超過判定にもコードポイント数を使う
```

## 観測と切り分け

| 確認対象 | 観測結果 | 判断 |
| --- | --- | --- |
| 入力 | `A😀B` | 上限ちょうどの3コードポイントである。 |
| 直接結果 | `accepted=false` | 受理・拒否の判定が契約と異なる。 |
| 計測結果 | `measuredCharacterCount=4` | 判定に使った単位が契約上の文字数と異なる。 |
| 最終状態 | `registeredNames=[]` | 応答値だけではなく、受理後に必要な状態も失われている。 |
| `String`の実装 | `displayName.length()` | UTF-16コード単位の数を使っている。 |

別の原因候補として、レジストリへの追加漏れも考えられます。しかし、登録前の判定結果が`false`であり、計測値が4になっているため、登録処理ではなく計測単位が直接原因だと絞り込めます。

## 原因

Javaの`String`はUTF-16形式で表現され、補助文字はサロゲートペアとして2つの`char`位置を使います。`String.length()`はこの`char`コード単位数を返します。一方、`String.codePointCount(beginIndex, endIndex)`は指定範囲のUnicodeコードポイント数を返します。[String API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/String.html)

バグ状態では、利用者向けの文字数上限という契約に対して`length()`を使っていました。そのため、補助文字を含む`A😀B`を4として扱い、上限を超えたと判定していました。

## 修正

測定を`length()`から`codePointCount()`へ変更します。

```java
int measuredCharacterCount = displayName.codePointCount(0, displayName.length());
```

この修正は、上限をUnicodeコードポイント単位で数えるという本ラボの契約に一致します。登録処理、レジストリ、エラーメッセージの責務は変更していません。

## 回帰確認

修正後の`main`で同じテストを実行します。

```bash
git checkout main
./scripts/test.sh
```

テストは、受理結果、計測値、`contains`、登録済み集合を別々に検証します。したがって、将来、判定だけを直して登録を忘れる変更や、超過入力の計測単位が戻る変更も検出できます。

## 設計上の制約

コードポイント数は画面上の見た目の文字数と常に一致するわけではありません。結合文字列や絵文字のZWJシーケンスを1文字と数える要件がある場合には、グラフェムクラスタを基準にする別の設計が必要です。本ラボは、UTF-16コード単位とUnicodeコードポイントの取り違えだけを扱います。
