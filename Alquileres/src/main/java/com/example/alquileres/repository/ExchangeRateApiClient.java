package com.example.alquileres.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "exchangeRateApiClient", url = "https://api.exchangerate-api.com/v4/latest/")
public interface ExchangeRateApiClient {

    @GetMapping("{baseCurrency}?apiKey=1dba07c3a21fe8931df0aca2")
    Map<String, Object> getExchangeRate(@PathVariable("baseCurrency") String baseCurrency);

}
