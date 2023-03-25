package com.kvn.batch.process.products;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;


public class ProductItemProcessor implements ItemProcessor<Product, Product>{

	Logger logger = LoggerFactory.getLogger(ProductItemProcessor.class);
	@Override
	public Product process(Product item) throws Exception {
		logger.info("Product in Process:"+item.getProductId());
		item.setDescription(item.getDescription()!=null?item.getDescription().toUpperCase():item.getDescription());
		return item;
	}
}
