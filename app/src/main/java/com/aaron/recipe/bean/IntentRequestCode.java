package com.aaron.recipe.bean;

public enum IntentRequestCode
{
    SETTINGS(1), ABOUT(2), LOGS(3), RECIPE(4);

    private int code;

    IntentRequestCode(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public static boolean isValid(int code)
    {
        for(IntentRequestCode request : values())
        {
            if(request.getCode() == code)
            {
                return true;
            }
        }

        return false;
    }
}