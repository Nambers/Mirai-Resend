//Copyright (C) 2020-2021 Eritque arcus and contributors.
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as
//published by the Free Software Foundation, either version 3 of the
//License, or any later version(in your opinion).
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.
package tech.eritquearcus

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