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

        // Call stored procedure (assumes JSON output in one column)
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("identifier", identifier);

        // Call stored procedure / query
        return jdbcTemplate.queryForObject(
                procedureName, // e.g., "EXEC get_invoice_mock_data :identifier"
                params,
                String.class);
    }
}
