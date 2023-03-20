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
import net.mamoe.mirai.message.data.MessageChain
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
    var config = Config(mutableListOf(), mutableListOf())

    private fun MatchResult.buildNewMessage(): MessageChain =
        buildMessageChain {
            TODO()
        }

    private fun matchResendConditionOrNull(
        msg: MessageChain,
        resends: MutableList<Config.ResendCommand>
    ): MatchResult? {
        if (resends.isEmpty() || msg.isEmpty()) return null
        val index = resends.indices
        for (i in index) {
            val matched = if (resends[i].regex == false) {
                if (resends[i].matchMiraiCode == false) msg.contentToString() == resends[i].target
                else msg.serializeToMiraiCode() == resends[i].target
            } else {
                TODO()
            }
            if (matched) {
                return MatchResult(resends[i])
            }
        }
        return null
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
                val r = matchResendConditionOrNull(this.message, config.resendsForGroup)
                if (r != null) {
                    if (config.intercept == true) this.intercept()
                    GroupMessageEvent(
                        this.senderName,
                        this.permission,
                        this.sender,
                        r.buildNewMessage(),
                        this.time
                    ).broadcast()
                }
            }
        if (config.blockFriend != true)
            GlobalEventChannel.subscribeAlways<FriendMessageEvent>(priority = if (config.intercept == true) EventPriority.HIGHEST else EventPriority.MONITOR) {
                val r = matchResendConditionOrNull(this.message, config.resendsForGroup)
                if (r != null) {
                    FriendMessageEvent(
                        this.sender,
                        r.buildNewMessage(),
                        this.time
                    ).broadcast()

                }
            }
    }
}