package de.oliver.fancycoins;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class FancyCoinsLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("JitPack", "default", "https://jitpack.io/").build());
        resolver.addDependency(new Dependency(new DefaultArtifact("com.github.FancyMcPlugins:FancyLib:225ba14e03"), null));

        classpathBuilder.addLibrary(resolver);
    }
}
