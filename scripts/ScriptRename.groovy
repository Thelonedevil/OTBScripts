import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.commands.loader.LoadedCommand
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

public class ResponseCmd {
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String RENAME_SUCCESS = "~%command.rename.success"
    public static final String RENAME_NAME_IN_USE = "~%command.rename.already.in.use"
}

public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    if (!Command.exists(sArgs.db, sArgs.argsList[0])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    if (Command.exists(sArgs.db, sArgs.argsList[1])) {
        String commandStr = ResponseCmd.RENAME_NAME_IN_USE + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    LoadedCommand command = Command.get(sArgs.db, sArgs.argsList[0]);
    if (sArgs.userLevel.getValue() < command.modifyingUserLevels.getNameModifyingUL().getValue()) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_USER_LEVEL + " " + sArgs.commandName + " change name of command '" + sArgs.argsList[0] + "'";
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }

    command.setName(sArgs.argsList[1]);
    Command.remove(sArgs.db, sArgs.argsList[0])
    Command.addCommandFromLoadedCommand(sArgs.db, command);
    String commandStr = ResponseCmd.RENAME_SUCCESS + " " + sArgs.argsList[0] + " " + sArgs.argsList[1];
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}
