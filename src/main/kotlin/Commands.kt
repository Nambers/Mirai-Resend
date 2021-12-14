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

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand

private fun checkDelicate(command: Config.ResendCommand, groupOrFriend: Int): Boolean {
    if (when (groupOrFriend) {
            0 -> {
                Resend.config.resendsForFriend.contains(command) || Resend.config.resendsForGroup.contains(command)
            }
            1 -> Resend.config.resendsForFriend.contains(command)
            2 -> Resend.config.resendsForGroup.contains(command)
            else -> throw IllegalArgumentException()
        }
    ) {
        Resend.logger.error("重发列表中已经存在该重发, 取消添加")
        return true
    }
    return false
}

private fun update(from: String, to: String, regex: Boolean, groupOrFriend: Int, miraicode: Boolean): Boolean {
    val command = Config.ResendCommand(from, to, regex, miraicode)
    if (checkDelicate(command, groupOrFriend)) return false
    when (groupOrFriend) {
        0 -> {
            Resend.config.resendsForGroup.add(command)
            Resend.config.resendsForFriend.add(command)
        }
        1 -> {
            Resend.config.resendsForGroup.add(command)
        }
        2 -> {
            Resend.config.resendsForFriend.add(command)
        }
    }
    Resend.fileLock.lock()
    Resend.configFile.writeText(Resend.gson.toJson(Resend.config))
    Resend.fileLock.unlock()
    return true
}

object ResendCommand : SimpleCommand(
    Resend, "重发", "resend",
    description = "重发一个消息, from是激活消息的文本, to是重发成什么信息, groupOrFriend 应用到群聊(1)还是好友(2)或者全部(0), regex是触发的字符串是不是正则形式 miraicode是重发的信息是不是miraicode形式"
) {
    @Handler
    suspend fun CommandSender.handle(
        from: String,
        to: String,
        groupOrFriend: Int = 0,
        regex: Boolean = false,
        miraicode: Boolean = false
    ) {
        if (update(from, to, regex, groupOrFriend, miraicode))
            sendMessage("成功")
    }
}


object RemoveResendCommand : SimpleCommand(
    Resend, "取消重发", "removeResend",
    description = "删除一个重发条件, 注意:在运行中删除重发条件可能会导致不可预料的报错"
) {
    private fun delGroup(command: Config.ResendCommand) = Resend.config.resendsForGroup.remove(command)
    private fun delFriend(command: Config.ResendCommand) = Resend.config.resendsForFriend.remove(command)

    @Handler
    suspend fun CommandSender.handle(
        from: String,
        to: String,
        groupOrFriend: Int = 0,
        regex: Boolean = false,
        miraicode: Boolean = false
    ) {
        val command = Config.ResendCommand(from, to, regex, miraicode)
        if (!when (groupOrFriend) {
                0 -> {
                    delGroup(command) && delFriend(command)
                }
                1 -> delGroup(command)
                2 -> delFriend(command)
                else -> throw IllegalArgumentException()
            }
        )
            sendMessage("失败, 可能的原因:列表中不存在")
        else
            sendMessage("成功")
    }
}

fun register() {
    CommandManager.registerCommand(ResendCommand)
    CommandManager.registerCommand(RemoveResendCommand)
}