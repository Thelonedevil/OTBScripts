import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.api.APIConfig
import com.github.otbproject.otbproject.channels.Channel
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String UNSILENCED = "~%bot.unsilence.success";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    Channel channel = APIChannel.get(sArgs.channel);

    switch (sArgs.argsList[0].toLowerCase()) {
        case "on":
        case "true":
            channel.getConfig().setSilenced(true);
            APIConfig.writeChannelConfig(sArgs.channel);
            channel.sendQueue.clear();
            return true;
        case "off":
        case "false":
            channel.getConfig().setSilenced(false);
            APIConfig.writeChannelConfig(sArgs.channel);
            String commandStr = ResponseCmd.UNSILENCED;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}
