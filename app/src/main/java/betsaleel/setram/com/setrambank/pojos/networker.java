package betsaleel.setram.com.setrambank.pojos;

/**
 * Created by hornellama on 01/02/2018.
 */

public class networker {
    public static String ipServer ="betsaleeltech.com";
    public static  String changePin="http://"+ipServer+"/codes/serveur/api/pinlog/";
    public static final String URL="http://www.betsaleeltech.com/setramvip/codes/serveur/api/";
    public static final String URL2="http://www.betsaleeltech.com/setramvip/codes/serveur/";

    public static  String getLoginUrlFormatted(String idcompte,String password){
        return  URL+"loginController.php?idcompte="+idcompte+"&pin="+password;
    }

    public static  String getLoginAddedUrlFormatted(String idcompte,String password){
        return  URL+"loginController.php?numcompte="+idcompte+"&pin="+password;
    }

    public static  String getUpdateLoginUrlFormatted(String account,String oldpwd,String newpwd){
        return  URL+"pinController.php?account="+account+"&old="+oldpwd+"&new="+newpwd;
    }

    public static  String getTransferUrlFormatted
            (
                    String idAccountExp,
                    String accountBenef,
                    String amount,
                    String codeMoney,
                    String clientType,
                    String idAgent,
                    String idAgence,
                    String pwd,
                    String accountType
            )
    {
        return  URL2+"ServeurTransfert_Mobile.php?IdCompteE="+idAccountExp+"&IdCompteB="+accountBenef+
                "&MontantTransfert="+amount+"&CodeMonnaietrans="+codeMoney+"&Moyen="+clientType+"&idagent="+idAgent+
                "&IdAgence="+idAgence+"&PIN="+pwd+"&CodeTypeCompte="+idAccountExp;
    }


}
