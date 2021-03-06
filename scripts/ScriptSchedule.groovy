import com.github.otbproject.otbproject.api.APISchedule
import com.github.otbproject.otbproject.commands.Command
import com.github.otbproject.otbproject.messages.send.MessagePriority
import com.github.otbproject.otbproject.proc.ScriptArgs
import com.github.otbproject.otbproject.util.BuiltinCommands
import com.github.otbproject.otbproject.util.ScriptHelper

import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

public class ResponseCmd {
    public static final String SUCCESSFULL_SCHEDULE = "~%schedule.success";
    public static final String GENERAL_DOES_NOT_EXIST = "~%command.general:does.not.exist";
    public static final String COMMAND_ALREADY_SCHEDULED = "~%schedule.already.scheduled";
    public static final String SUCCESSFULL_UNSCHEDULE = "~%unschedule.success";
    public static final String COMMAND_NOT_SCHEDULED = "~%unschedule.not.scheduled";
}
public boolean execute(ScriptArgs sArgs) {
    if (sArgs.argsList.length < 1) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    switch (sArgs.argsList[0].toLowerCase()) {
        case "add":
        case "new":
            return schedule(sArgs);
        case "remove":
        case "delete":
        case "del":
        case "rm":
            return unSchedule(sArgs);
        case "list":
            Set set = APISchedule.getScheduledCommands(sArgs.channel)
            List list = set.stream().sorted().collect(Collectors.toList())
            String asString = "Scheduled commands: " + list.toString();
            ScriptHelper.sendMessage(sArgs.destinationChannel, asString, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + action;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}

private boolean schedule(ScriptArgs sArgs) {
    // !schedule set 5 30 minutes true !test testarg
    if (sArgs.argsList.length < 6) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    int offset;
    try {
        offset = Integer.parseInt(sArgs.argsList[1]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[0];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    int period;
    try {
        period = Integer.parseInt(sArgs.argsList[2]);
    } catch (NumberFormatException e) {
        String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + sArgs.argsList[1];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    String timeUnit = sArgs.argsList[3].toUpperCase();
    boolean reset = !sArgs.argsList[4].equals("false");
    String command = String.join(" ", sArgs.argsList[5..-1]);
    if (!Command.exists(sArgs.db, sArgs.argsList[6])) {
        String commandStr = ResponseCmd.GENERAL_DOES_NOT_EXIST + " " + sArgs.argsList[5];
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    if (APISchedule.isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_ALREADY_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    switch (TimeUnit.valueOf(timeUnit)) {
        case TimeUnit.MINUTES:
            APISchedule.scheduleCommandInMinutes(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.HOURS:
            APISchedule.scheduleCommandInHours(sArgs.channel, command, offset, period)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        case TimeUnit.SECONDS:
            APISchedule.scheduleCommandInSeconds(sArgs.channel, command, offset, period, reset)
            String commandStr = ResponseCmd.SUCCESSFULL_SCHEDULE + " " + command;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return true;
        default:
            String commandStr = BuiltinCommands.GENERAL_INVALID_ARG + " " + sArgs.commandName + " " + timeUnit;
            ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
            return false;
    }
}


private boolean unSchedule(ScriptArgs sArgs) {
    //!schedule remove !test
    if (sArgs.argsList.length < 2) {
        String commandStr = BuiltinCommands.GENERAL_INSUFFICIENT_ARGS + " " + sArgs.commandName;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    String command = String.join(" ", sArgs.argsList[1..-1]);
    if (!APISchedule.isScheduled(sArgs.channel, command)) {
        String commandStr = ResponseCmd.COMMAND_NOT_SCHEDULED + " " + command;
        ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
        return false;
    }
    APISchedule.unScheduleCommand(sArgs.channel, command);
    APISchedule.removeFromDatabase(sArgs.channel, command);
    String commandStr = ResponseCmd.SUCCESSFULL_UNSCHEDULE + " " + command;
    ScriptHelper.runCommand(commandStr, sArgs.user, sArgs.channel, sArgs.destinationChannel, MessagePriority.HIGH);
    return true;
}

