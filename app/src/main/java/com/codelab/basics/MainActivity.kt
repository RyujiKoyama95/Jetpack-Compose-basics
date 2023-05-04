/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.basics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.support.v4.os.IResultReceiver2.Default
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.basics.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentでレイアウトを設定するが、XMLファイルではなく、@Composable関数を呼び出す
        // XMLファイルを指定する場合はsetContentView()
        setContent { 
            BasicsCodelabTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }

    @Composable
    private fun MyApp(modifier: Modifier = Modifier) {
        // remember関数はComposableがコンポジション内で保持されている場合のみ、機能する。
        // アクティビティのリセット(画面回転やプロセスの終了等)が起こると、全ての状態が消えてしまう。
        // この場合はrememberSaveable関数を使用する。
        var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }
        Surface(modifier) {
            if (shouldShowOnboarding) {
                OnboardingScreen(onContinueClicked = {
                    shouldShowOnboarding = false
                })
            } else {
                Greetings()
            }
        }
    }

    @Preview(
        showBackground = true,
        widthDp = 320,
        uiMode = UI_MODE_NIGHT_YES,
        name = "Dark"
    )

    @Preview(showBackground = true, name = "text preview")
    @Composable
    private fun DefaultPreview() {
        BasicsCodelabTheme {
            MyApp()
        }
    }

    @Preview
    @Composable
    private fun MyAppPreview() {
        BasicsCodelabTheme {
            MyApp(Modifier.fillMaxSize())
        }
    }

    @Composable
    private fun Greetings(
        names: List<String> = List(1000) { "$it" },
        modifier: Modifier = Modifier
    ) {
        // Column{}の場合、数千のリストを表示すると、全てをレンダリングするので、フリーズの原因になる。
        // 画面領域のみ表示されているリストをレンダリングするLazyColumn{}を使用するべき。
        // ViewのRecyclerViewと似たもの。
        LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
            // LazyColumnの使用方法としては、基本的にスコープ内でitemsを使用し、
            // この中にリストの個々のアイテムを表示するロジックを書く。
            items(items = names) {name ->
                Greeting(name = name)
            }
        }
    }

    @Preview
    @Composable
    private fun GreetingsPreview() {
        BasicsCodelabTheme {
            Greetings()
        }
    }

    @Composable
    private fun Greeting(name: String) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            CardContent(name = name)
        }
    }
    @Composable
    private fun OnboardingScreen(
        modifier: Modifier = Modifier,
        onContinueClicked: () -> Unit
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to the Basics Codelab!")
            Button(
                modifier = modifier.padding(24.dp),
                onClick = onContinueClicked
            ) {
                Text(text = "Continue")
            }
        }
    }

    @Preview
    @Composable
    private fun OnboardingPreview() {
        BasicsCodelabTheme {
            OnboardingScreen(onContinueClicked = {})
        }
    }

    @Composable
    private fun CardContent(name: String) {
        // 状態保持のためexpandedフラグを定義。
        // 単純にフラグ追加しても、Composeは状態変更として検出しない。
        // state/MutableStateは何らかの値を保持し、値が変更した場合は再Compose(UI更新)するインターフェース。
        // しかし、再コンポジションが随時発生する可能性があるため、フラグはリセットされてしまう問題がある。
        // 再コンポジションの前後で状態を保持するにはrememberを使用して、状態を保護する。リセットされない。
        // 状態が変化すると、自動的に再コンポーズする。
        // また、同じコンポーズを別々の部分から呼び出すと、異なるUIが生成され、状態も別々になる。
        // 例えば今回の場合だと、ボタンが複数あるため、それぞれで固有の状態を保持する。
        var expanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .padding(24.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
            // SurfaceやTextなどのほとんどのCompose UI要素は引数にmodifier(修飾子)を持っている。
            // 1つの要素に複数の修飾子をつけるときは、繋げればいい。
            Column(modifier = Modifier
                .weight(1f)
                .padding(12.dp)
            ) {
                // modifierは親レイアウト内での配置、表示、動作を指定できる。
                Text(text = "Hello!")
                Text(
                    text = "$name",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                if (expanded) {
                    Text(
                        text = ("Composem ipsum color sit lazy, " +
                                "padding theme elit, sed do bouncy. ").repeat(4)
                    )
                }
            }
            IconButton(onClick = { expanded = !expanded }) {
                // imageVectorはベクターのマテリアルアイコン
                // contentDescriptionはアイコンの説明。nullでもOK
                Icon(
                    imageVector = if (expanded) Filled.ExpandLess else Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(id = R.string.show_less)
                    } else {
                        stringResource(id = R.string.show_more)
                    }
                )
            }
        }
    }
}