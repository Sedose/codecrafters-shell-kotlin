package io.codecrafters

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

class ConsistTest {
    /*
        If this test fails, consider using composition and not inheritance
        https://www.youtube.com/watch?v=-n6784KeQMs
        https://www.youtube.com/watch?v=hxGOiiR9ZKg&t=205s
        https://www.youtube.com/watch?v=da_Rvn0au-g
        If not, talk to a team to make one more exempt from inheritance restriction
     */
    @Test
    fun `restrict inheritance`() {
        Konsist
            .scopeFromProject()
            .classes()
            .filterNot(isExemptFromInheritanceRestriction)
            .assertTrue(function = onlyExtendsInterfaces)
    }

    private val isExemptFromInheritanceRestriction: (KoClassDeclaration) -> Boolean = { classDef ->
        classDef.hasParentWithName("RuntimeException") ||
            classDef.hasParentWithName("CommandLineRunner")
    }

    private val onlyExtendsInterfaces: (KoClassDeclaration) -> Boolean = { classDef ->
        classDef
            .parents()
            .map { it.sourceDeclaration }
            .all { it is KoInterfaceDeclaration }
    }
}
