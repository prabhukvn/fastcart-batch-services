package com.kvn.batch.process.products;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ProductsRowMapper implements RowMapper<Product> {
	
	
	
	@Override
	public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
	
		Product product = new Product();
		product.setProductId(rs.getString("PRODUCT_ID"));
		product.setDisplayName(rs.getString("DISPLAY_NAME"));
		product.setDescription(rs.getString("DESCRIPTION"));
		product.setBrand(rs.getString("BRAND"));
		return product;
	}
	
	

}
