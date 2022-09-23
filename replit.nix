{ pkgs }: {
    deps = [
        pkgs.graalvm17-ce
        # jdk17_headless
        pkgs.maven
        pkgs.replitPackages.jdt-language-server
        pkgs.replitPackages.java-debug
    ];
}