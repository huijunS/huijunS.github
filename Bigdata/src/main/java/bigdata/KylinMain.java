package shj;

public class KylinMain {

    public static void main(String[] args) {
        String output ;
        KylinTest.login("ADMIN","KYLIN");
        String body = "{\"sql\":\"select * from \",\"offset\":0,\"limit\":50000,\"acceptPartial\":false,\"project\":\"TEST\"}";
        //output = KylinTest.query(body);
        //output = KylinTest.buildCube("my_kylin_cube",body);
        //output = KylinTest.listQueryableTables("TEST");
       // output = KylinTest.listCubes(0,15,"shj_cube", "TEST");
        body = "{\"startTime\": 1451750400000,\"endTime\": 1451836800000,\"buildType\": \"BUILD\"}";

	output = KylinTest.buildCube("my_kylin_cube",body);
        System.out.println(output);
    }
}
