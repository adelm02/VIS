package interfaces;

import java.sql.SQLException;
//Command

public interface IReservationCommand {
    void execute() throws SQLException;
    String getCommandName();
}