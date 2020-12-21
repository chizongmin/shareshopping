package base

enum Code {
    accountPasswordError(1101, "账号或密码错误")












    static fillCode(code){
        return [code:code.code,message:code.name]
    }
    private String code
    private String name
    Code(int code, String name) {
        this.code = code
        this.name = name
    }
}
