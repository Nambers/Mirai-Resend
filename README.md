# Mirai-Resend

重发插件, 设置一条重发条件后, 当该条件被触发, 相当于机器人收到另外一条信息

## 配置文件

```kotlin
data class Config(
    // 好友的重发条件
    val resendsForFriend: ArrayList<ResendCommand>,
    // 群聊的重发条件
    val resendsForGroup: ArrayList<ResendCommand>,
    // 匹配消息的content(MessageChain.contentToString)
    val content: Boolean? = null,
    // 匹配消息的MiraiCode(MessageChain.serializeToMiraiCode)
    val miraiCode: Boolean? = null,
    // 阻止其他监听器获取重发前的信息
    val intercept: Boolean? = null,
    // 屏蔽全部好友信息
    val blockFriend: Boolean? = null,
    // 屏蔽全部群聊信息
    val blockGroup: Boolean? = null,
    // 每条信息只匹配一次
    val matchOnce: Boolean? = null
) {
    data class ResendCommand(
        // 触发字符串, 可为正则表达式
        val target: String,
        // 重发字符串, 可为MiraiCode
        val to: String,
        // 触发字符串是否是正则表达式
        val regex: Boolean? = null,
        // 重发字符串是否为MiraiCode
        val miraiCode: Boolean? = null
    )
}
```

例子:

```shell
// 下文 `//` 开头的注释只是解释作用, 实际上的json并不能存在注释
{
  "resendsForFriend": [ // 好友重发条件
    {
      "target": "a", // 触发字符串
      "to": "b", // 重发字符串
      "regex": false,
      "miraiCode": false
    }
  ],
  "resendsForGroup": [
    {
      "target": "a",
      "to": "b",
      "regex": false,
      "miraiCode": false
    }
  ]
}
```