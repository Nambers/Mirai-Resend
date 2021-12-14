//Copyright (C) 2021-2021 Eritque arcus and contributors.
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

import com.google.gson.Gson
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.broadcast
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.code.MiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildMessageChain
import java.io.File
import java.util.concurrent.locks.ReentrantLock

object Resend : KotlinPlugin(
    JvmPluginDescription(
        id = "tech.eritquearcus.Resend",
        name = "resend",
        version = "1.0",
    ) {
        author("Eritque arcus")
    }
) {
    val fileLock: ReentrantLock = ReentrantLock()
    val gson = Gson()
    val configFile = File(this.configFolder.absolutePath, "config.json")
    var config = Config(ArrayList(), ArrayList())
    private fun Config.ResendCommand.equal(msg: MessageChain): Boolean {
        val text = when {
            config.miraiCode == true -> msg.contentToString()
            config.content == true -> msg.serializeToMiraiCode()
            else -> msg.contentToString()
        }
        return when (regex) {
            true -> Regex(this.target).matches(text)
            null,
            false -> text == this.target
        }
    }

    private fun MessageChain.buildNewMessage(text: String, miraiCode: Boolean?): MessageChain =
        buildMessageChain {
            +(if (miraiCode == null || miraiCode == false)
                PlainText(text)
            else
                MiraiCode.deserializeMiraiCode(text))
            +this@buildNewMessage[MessageSource]!!
        }

    override fun onEnable() {
        logger.info("Resend Plugin loaded")
        logger.info("配置文件地址: ${configFile.absolutePath}")
        if (!configFile.isFile || !configFile.exists()) {
            logger.info("配置文件不存在, 已新建")
            configFile.createNewFile()
            configFile.writeText(gson.toJson(config))
        }
        config = gson.fromJson(configFile.readText(), Config::class.java)
        register()
        if (config.blockGroup != true)
            GlobalEventChannel.subscribeAlways<GroupMessageEvent>(priority = if (config.intercept == true) EventPriority.HIGHEST else EventPriority.MONITOR) {
                if (config.intercept == true) this.intercept()
                if (config.resendsForGroup.isEmpty()) return@subscribeAlways
                val index = config.resendsForGroup.indices
                // 索引循环, 避免循环途中插入导致的问题, 用锁也可以解决但是太重了
                for (i in index) {
                    val resend = config.resendsForGroup[i]
                    if (resend.equal(this.message)) {
                        GroupMessageEvent(
                            this.senderName,
                            this.permission,
                            this.sender,
                            this.message.buildNewMessage(resend.to, resend.miraiCode),
                            this.time
                        ).broadcast()
                        if (config.matchOnce == true) return@subscribeAlways
                    }
                }
            }
        if (config.blockFriend != true)
            GlobalEventChannel.subscribeAlways<FriendMessageEvent>(priority = if (config.intercept == true) EventPriority.HIGHEST else EventPriority.MONITOR) {
                if (config.intercept == true) this.intercept()
                if (config.resendsForFriend.isEmpty()) return@subscribeAlways
                val index = config.resendsForFriend.indices
                // 索引循环, 避免循环途中插入导致的问题, 用锁也可以解决但是太重了
                for (i in index) {
                    val resend = config.resendsForFriend[i]
                    if (resend.equal(this.message)) {
                        FriendMessageEvent(
                            this.sender,
                            this.message.buildNewMessage(resend.to, resend.miraiCode),
                            this.time
                        ).broadcast()
                        if (config.matchOnce == true) return@subscribeAlways
                    }
                }
            }
    }
}