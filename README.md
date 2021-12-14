# Mirai-Resend

重发插件, 设置一条重发条件后, 当该条件被触发, 相当于机器人收到另外一条信息

原型: [miraiForum](https://mirai.mamoe.net/topic/848/%E6%9C%89%E6%B2%A1%E6%9C%89%E4%BB%80%E4%B9%88%E5%8A%9E%E6%B3%95%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8F%92%E4%BB%B6%E5%9B%9E%E5%A4%8D%E7%9A%84%E8%A7%A6%E5%8F%91%E8%AF%AD%E5%8F%A5%E5%91%A2)

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
  "matchOne": false,
  "resendsForFriend": [ // 好友重发条件
    {
      "target": "a", // 触发字符串
      "to": "b", // 重发字符串
      "regex": false, // target是否是正则表达式
      "miraiCode": false // to是否是miraiCode
    },
    {
      "target": "c", 
      "to": "d",
      "regex": false,
      "miraiCode": false
    }
  ],
  "resendsForGroup": [ // 群聊重发条件
    {
      "target": "a",
      "to": "b",
      "regex": false,
      "miraiCode": false
    }
  ]
}
```
## 指令
有两个指令
- `/重发 <from> <to> [groupOrFriend] [regex] [miraicode]`    重发一个消息, from是激活消息的文本, to是重发成什么信息, groupOrFriend 应用到群聊(1)还是好友(2)或者全部(0), regex是触发的字符串是不是正则形式 miraicode是重发的信息是不是miraicode形式
- `/取消重发 <from> <to> [groupOrFriend] [regex] [miraicode]`    删除一个重发条件, 注意:在运行中删除重发条件可能会导致不可预料的报错

可以在`help`里看
