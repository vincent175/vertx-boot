package pers.vincent.vertxboot.persist;

import jodd.util.StringUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author: Vincent
 * @date: 2021/9/18
 */
@Component
@MappedTypes(LocalDate.class)
@MappedJdbcTypes(value = JdbcType.DATE, includeNullJdbcType = true)
public class CustomLocalDateTypeHandler extends BaseTypeHandler<LocalDate>  {

    private static final String datePattern = "yyyy-MM-dd";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null) {
            ps.setString(i, parameter.format(DateTimeFormatter.ofPattern(datePattern)));
        }
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String target = rs.getString(columnName);
        if (StringUtil.isEmpty(target)) {
            return null;
        }
        LocalDate parse = LocalDate.parse(target, DateTimeFormatter.ofPattern(datePattern));
        return parse;
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String target = rs.getString(columnIndex);
        if (StringUtil.isEmpty(target)) {
            return null;
        }
        LocalDate parse = LocalDate.parse(target, DateTimeFormatter.ofPattern(datePattern));
        return parse;
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String target = cs.getString(columnIndex);
        if (StringUtil.isEmpty(target)) {
            return null;
        }
        LocalDate parse = LocalDate.parse(target, DateTimeFormatter.ofPattern(datePattern));
        return parse;
    }
}
