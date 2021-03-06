package liquibase.ext.percona;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.change.core.DropUniqueConstraintChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;

/**
 * Subclasses the original {@link liquibase.change.core.DropUniqueConstraintChange} to
 * integrate with pt-online-schema-change.
 * @see PTOnlineSchemaChangeStatement
 */
@DatabaseChange(name = PerconaDropUniqueConstraintChange.NAME, description = "Drops an existing unique constraint",
    priority = PerconaDropUniqueConstraintChange.PRIORITY, appliesTo = "uniqueConstraint")
public class PerconaDropUniqueConstraintChange extends DropUniqueConstraintChange implements PerconaChange {
    public static final String NAME = "dropUniqueConstraint";
    public static final int PRIORITY = ChangeMetaData.PRIORITY_DEFAULT + 50;

    private Boolean usePercona;

    /**
     * Generates the statements required for the drop unique constraint change.
     * In case of a MySQL database, percona toolkit will be used.
     * In case of generating the SQL statements for review (updateSQL) the command
     * will be added as a comment.
     * @param database the database
     * @return the list of statements
     * @see PTOnlineSchemaChangeStatement
     */
    @Override
    public SqlStatement[] generateStatements(Database database) {
        return PerconaChangeUtil.generateStatements(this,
                database,
                super.generateStatements(database));
    }

    @Override
    public String generateAlterStatement(Database database) {
        StringBuilder alter = new StringBuilder();

        alter.append("DROP KEY ");
        alter.append(database.escapeConstraintName(getConstraintName()));

        return alter.toString();
    }

    @Override
    public Boolean getUsePercona() {
        return usePercona;
    }

    public void setUsePercona(Boolean usePercona) {
        this.usePercona = usePercona;
    }

    @Override
    public String getChangeSkipName() {
        return NAME;
    }

    @Override
    public String getTargetTableName() {
        return getTableName();
    }
}
