package com.demo.recipeapp.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.demo.recipeapp.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Database(entities = [Recipe::class,Ingredient::class,Step::class], version = 1, exportSchema = false)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    class Callback @Inject constructor(
        private val database: Provider<RecipeDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val recipeDao = database.get().recipeDao()

            applicationScope.launch {
                val id1: Long = recipeDao.insertRecipe(Recipe("Crunchy Croissants"
                    ,"Buttery, flaky pastry named for its crescent shape",
                    "Breakfast","",""))
                recipeDao.insertIngredient(Ingredient(id1,"1 (.25 ounce) package active dry yeast"))
                recipeDao.insertIngredient(Ingredient(id1,"1 cup warm water (110 degrees F/45 degrees C)"))
                recipeDao.insertIngredient(Ingredient(id1,"¾ cup evaporated milk"))
                recipeDao.insertIngredient(Ingredient(id1,"1 ½ teaspoons salt"))
                recipeDao.insertIngredient(Ingredient(id1,"⅓ cup white sugar"))
                recipeDao.insertIngredient(Ingredient(id1,"1 egg"))
                recipeDao.insertIngredient(Ingredient(id1,"5 cups all-purpose flour, divided"))
                recipeDao.insertIngredient(Ingredient(id1,"¼ cup butter, melted"))
                recipeDao.insertIngredient(Ingredient(id1,"1 cup butter, chilled and diced"))
                recipeDao.insertIngredient(Ingredient(id1,"1 egg, beaten"))

                recipeDao.insertStep(Step(id1,"Brush croissants with beaten egg. Bake in preheated oven for 35 minutes, until golden."))
                recipeDao.insertStep(Step(id1,"Cover and let rise at room temperature until almost doubled in size. Approximately 2 hours. Meanwhile, preheat oven to 325 degrees F (165 degrees C)."))
                recipeDao.insertStep(Step(id1,"Roll one part of the dough on a floured board into a circle 17 inches in diameter. With a sharp knife or pizza cutter, cut the circle into eight equal pie-shaped wedges. Roll the wedges loosely toward the point. Shape each roll into a crescent and place on an ungreased baking sheet. Allow 1 1/2 inches space between each roll."))
                recipeDao.insertStep(Step(id1,"Turn dough out onto a floured surface; press into compact balls and knead about 6 turns to release air bubbles. Divide dough into four equal parts. Shape one at a time. Refrigerate the remaining dough."))
                recipeDao.insertStep(Step(id1,"In a large bowl, cut the one cup firm butter into remaining four cups flour until butter particles are the size of dried kidney beans. Pour the yeast batter over this and carefully turn the mixture over with a spatula to blend, just until all flour is moistened. Cover with plastic wrap and refrigerate until well chilled, at least 4 hours or up to four days."))
                recipeDao.insertStep(Step(id1,"In a large bowl, dissolve yeast in warm water. Let stand until creamy, about 10 minutes. Stir in milk, salt, sugar, 1 egg, 1 cup flour and melted butter. Beat to make a smooth batter; set aside."))



                val id2: Long = recipeDao.insertRecipe(Recipe("Classic Waffles"
                    ,"A lovely, crispy waffle perfect for the morning.",
                    "Breakfast","",""))
                recipeDao.insertIngredient(Ingredient(id2,"2 cups all-purpose flour"))
                recipeDao.insertIngredient(Ingredient(id2,"1 teaspoon salt"))
                recipeDao.insertIngredient(Ingredient(id2,"4 teaspoons baking powder"))
                recipeDao.insertIngredient(Ingredient(id2,"2 tablespoons white sugar"))
                recipeDao.insertIngredient(Ingredient(id2,"2 eggs"))
                recipeDao.insertIngredient(Ingredient(id2,"1 ½ cups warm milk"))
                recipeDao.insertIngredient(Ingredient(id2,"⅓ cup butter, melted"))
                recipeDao.insertIngredient(Ingredient(id2,"1 teaspoon vanilla extract"))

                recipeDao.insertStep(Step(id2,"Ladle the batter into a preheated waffle iron. Cook the waffles until golden and crisp. Serve immediately."))
                recipeDao.insertStep(Step(id2,"In a separate bowl, beat the eggs. Stir in the milk, butter and vanilla. Pour the milk mixture into the flour mixture; beat until blended."))
                recipeDao.insertStep(Step(id2,"In a large bowl, mix together flour, salt, baking powder and sugar; set aside. Preheat waffle iron to desired temperature."))

                val id3: Long = recipeDao.insertRecipe(Recipe("Leftover Salmon Lunch Wrap"
                    ,"I am currently eating clean so I needed a quick and healthy lunch recipe. I already had pepper slices and leftover salmon in the refrigerator so I just decided to throw something together! This recipe is fresh and delicious and easily doubled for multiple people.",
                    "Lunch","",""))
                recipeDao.insertIngredient(Ingredient(id3,"1 (15 ounce) can black beans, drained"))
                recipeDao.insertIngredient(Ingredient(id3,"1 (3 ounce) fillet cooked salmon"))
                recipeDao.insertIngredient(Ingredient(id3,"1 red bell pepper, thinly sliced"))
                recipeDao.insertIngredient(Ingredient(id3,"1 green bell pepper, thinly sliced"))
                recipeDao.insertIngredient(Ingredient(id3,"1 small avocado, halved and sliced"))

                recipeDao.insertStep(Step(id3,"Divide black beans and salmon evenly between the tortillas. Cover with red bell pepper, green bell pepper, and avocado slices. Wrap up tortillas."))
                recipeDao.insertStep(Step(id3,"Reheat salmon in a covered skillet over medium-low heat, 3 to 5 minutes per side. Slice into smaller pieces."))
                recipeDao.insertStep(Step(id3,"Heat black beans in a small saucepan over medium-low heat until warmed through, about 5 minutes."))


                val id4: Long = recipeDao.insertRecipe(Recipe("Dessert Yogurt Protein Bowl"
                    ,"This is a wonderful healthy alternative to dessert. It's full of protein and would also make a great breakfast. Use your favorite protein bar or granola.",
                    "Dessert","",""))
                recipeDao.insertIngredient(Ingredient(id4,"¼ cup Greek yogurt"))
                recipeDao.insertIngredient(Ingredient(id4,"1 tablespoon peanut butter"))
                recipeDao.insertIngredient(Ingredient(id4,"½ chocolate protein bar, cut into small pieces"))
                recipeDao.insertIngredient(Ingredient(id4,"5 fresh strawberries, sliced"))

                recipeDao.insertStep(Step(id4,"Combine Greek yogurt and peanut butter in a bowl and whip together until smooth. Top with protein bar pieces and strawberries."))

                val id5: Long = recipeDao.insertRecipe(Recipe("Strawberry Delight Dessert Salad"
                    ,"A sweet and easy dessert salad that combines whipped topping, gelatin and fruit.",
                    "Dessert","",""))
                recipeDao.insertIngredient(Ingredient(id5,"1 (16 ounce) container frozen whipped topping, thawed"))
                recipeDao.insertIngredient(Ingredient(id5,"1 (6 ounce) package strawberry flavored Jell-O®"))
                recipeDao.insertIngredient(Ingredient(id5,"1 (11 ounce) can mandarin oranges, drained"))
                recipeDao.insertIngredient(Ingredient(id5,"2 cups grapes"))
                recipeDao.insertIngredient(Ingredient(id5,"2 cups miniature marshmallows"))

                recipeDao.insertStep(Step(id5,"In a large bowl, combine the thawed whipped topping, gelatin, fruit cocktail, oranges, grapes and marshmallows. Mix together well and refrigerate until chilled. Stir again before serving."))


                val id6: Long = recipeDao.insertRecipe(Recipe("Garlic and Parmesan Dinner Rolls"
                    ,"These garlic and Parmesan cheese dinner rolls are really great – crusty and cheesy on the outside, tender and garlicky inside, and visually gorgeous.",
                    "Dinner","",""))
                recipeDao.insertIngredient(Ingredient(id6,"1 (.25 ounce) package active dry yeast"))
                recipeDao.insertIngredient(Ingredient(id6,"½ cup all-purpose flour"))
                recipeDao.insertIngredient(Ingredient(id6,"½ teaspoon white sugar"))
                recipeDao.insertIngredient(Ingredient(id6,"1 cup warm water - 100 to 110 degrees F (40 to 45 degrees C)"))
                recipeDao.insertIngredient(Ingredient(id6,"1 teaspoon fine salt"))
                recipeDao.insertIngredient(Ingredient(id6,"1 tablespoon olive oil"))
                recipeDao.insertIngredient(Ingredient(id6,"1 egg"))
                recipeDao.insertIngredient(Ingredient(id6,"1 ¾ cups all-purpose flour"))
                recipeDao.insertIngredient(Ingredient(id6,"1 ½ tablespoons melted butter"))
                recipeDao.insertIngredient(Ingredient(id6,"2 cloves garlic, crushed"))
                recipeDao.insertIngredient(Ingredient(id6,"½ cup freshly grated Parmigiano-Reggiano cheese"))
                recipeDao.insertIngredient(Ingredient(id6,"½ teaspoon ground black pepper, or to taste"))
                recipeDao.insertIngredient(Ingredient(id6,"¼ teaspoon cayenne pepper, or more to taste"))
                recipeDao.insertIngredient(Ingredient(id6,"2 tablespoons freshly chopped Italian parsley"))
                recipeDao.insertIngredient(Ingredient(id6,"olive oil for brushing"))
                recipeDao.insertIngredient(Ingredient(id6,"¼ cup freshly grated Parmigiano-Reggiano cheese"))

                recipeDao.insertStep(Step(id6,"Bake in preheated oven until the tops are golden brown, about 20 minutes."))
                recipeDao.insertStep(Step(id6,"Brush each roll with olive oil. Sprinkle with more cheese. Cover and let rise until slightly puffed, about 20 to 30 minutes."))
                recipeDao.insertStep(Step(id6,"Preheat an oven to 400 degrees F (200 degrees C)."))
                recipeDao.insertStep(Step(id6,"Cut into 8 rolls. Transfer to a baking sheet, cut side up. If necessary, cut an ‘X' in the top of each roll to expose filling."))
                recipeDao.insertStep(Step(id6,"Press flat one long edge of the rectangle and brush with water. From the opposite end, roll evenly into a log. Press the seam together to seal."))
                recipeDao.insertStep(Step(id6,"Punch down and scrape the dough from the sides of the bowl. Turn onto a lightly floured surface. Sprinkle with more flour and shape into a rectangle. Roll out to an approximately 10x12-inch rectangle, about 1/2-inch thick. Brush with melted butter; sprinkle with garlic, Parmagiano-Reggiano cheese, black pepper, cayenne, and parsley."))
                recipeDao.insertStep(Step(id6,"Stir in salt, olive oil, and egg. Mix thoroughly and stir in remaining 1 3/4 cup flour to form a loose, sticky dough that pulls away from the sides of the bowl. Cover with a damp towel and let rise in a warm place until doubled in size, about 1 to 1 1/2 hours."))
                recipeDao.insertStep(Step(id6,"Mix yeast, 1/2 cup of flour, sugar, and warm water in a bowl. Cover and let rest in a warm place until bubbling, about 15 to 20 minutes."))
            }
        }
    }
}