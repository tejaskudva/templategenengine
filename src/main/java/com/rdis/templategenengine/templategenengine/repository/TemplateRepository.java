package com.rdis.templategenengine.templategenengine.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TemplateRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    // @formatter:off
    /**
     * Executes a named stored procedure or query to retrieve document data as a JSON string.
     * The procedure is expected to return a single row with a single column containing the JSON output.
     * This method is a helper for the data fetching process.
     *
     * @param identifier The unique identifier for the document, passed as a parameter to the procedure.
     * @param procedureName The name of the stored procedure to execute, which can include placeholder syntax (e.g., "EXEC get_invoice_data :identifier").
     * @return A String containing the JSON data returned by the stored procedure.
     */
    // @formatter:on
    public String fetchDocumentDataFromProc(String identifier, String procedureName) {

        String fullProcedureCall = "EXEC " + procedureName + " :invoiceId";

        // Call stored procedure (assumes JSON output in one column)
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("invoiceId", identifier);

        // Call stored procedure / query
        return jdbcTemplate.queryForObject(
                fullProcedureCall, // e.g., "EXEC get_invoice_mock_data :identifier"
                params,
                String.class);
    }

    public String getSTypeUserSessionId() {
        String query = "select top 1 randomnumber from pdbconnection where usertype = :userType";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userType", "S");

        return jdbcTemplate.queryForObject(query, params, String.class);
    }
}
