package mobile.application.footcardz.entity.enumeration;

public enum Position {
    ST, RW, LW, CAM, CM, CDM, RM, LM, CB, RB, LB, GK;

    public static boolean isValid(String position) {
        for (Position p : Position.values()) {
            if (p.name().equals(position)) {
                return true;
            }
        }
        return false;
    }
}
