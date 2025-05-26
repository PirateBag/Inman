package com.inman.model.request;

import com.inman.entity.Item;

public record ItemCrudBatch(Item[] updatedRows ) {};



