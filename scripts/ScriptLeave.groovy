import com.github.otbproject.otbproject.api.APIBot
import com.github.otbproject.otbproject.api.APIChannel
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String BOT_LEAVE_NEED_NAME = "~%bot.leave.need.name";
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = ResponseCmd.BOT_LEAVE_NEED_NAME;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    if (sArgs.argsList[0].equalsIgnoreCase(APIBot.getBot().getUserName())) {
        APIChannel.leave(sArgs.channel);
        return true;
    }

    return false;
}