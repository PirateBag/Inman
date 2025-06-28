package com.inman.entity;


//PLANNED:  No children, order qty OK, assigned must be zero.  Can be deleted.  Does not impact planningb.
//Open:  line items created for MOs, or insertable for POs.
//         Line items or order header can be assigned.
//      impacts planning
//Closed:  Cannot change any amounts.  Can be deleted with or without line amounts.
//  When closed, quantities are posted...
//  Does not impact planning.

public enum OrderState {
    PLANNED, OPEN, CLOSED;
}
