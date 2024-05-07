package controller

import (
	"fmt"
	"log"
	"net/http"
	"time"

	"example.com/m/database"
	"example.com/m/helpers"
	"example.com/m/models"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"golang.org/x/crypto/bcrypt"
)

var userCollection *mongo.Collection = database.OpenConnection(database.Client, "user")

var validate = validator.New()

func HashPassword(password string) string {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	if err != nil {
		log.Panic(err)
	}
	return string(bytes)

}

func VarifyPassword(userPassword string, providedPassword string) (bool, string) {
	err := bcrypt.CompareHashAndPassword([]byte(providedPassword), []byte(userPassword))
	check := true
	msg := ""

	if err != nil {
		msg = fmt.Sprintf("password of emial is incorrect")
		check = false
	}
	return check, msg
}

func Signup() gin.HandlerFunc {
	return func(c *gin.Context) {
		var user models.User
		err := c.BindJSON(&user)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}

		validationErr := validate.Struct(user)
		if validationErr != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": validationErr.Error(),
			})
			return
		}

		var count int = 0

		for _, userInDatabse := range database.ListOfAllUsers {
			if userInDatabse.Email == user.Email {
				count++
			}
		}
		password := HashPassword(user.Password)
		user.Password = password
		for _, userInDatabse := range database.ListOfAllUsers {
			if userInDatabse.Phone == user.Phone {
				count++
			}
		}

		if count > 0 {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": "this email or phone number already exists",
			})
		}

		user.Created_at, _ = time.Parse(time.RFC3339, time.Now().Format(time.RFC3339))
		user.Updated_at, _ = time.Parse(time.RFC3339, time.Now().Format(time.RFC3339))
		user.ID = primitive.NewObjectID()

		user.User_id = user.ID.Hex()

		token, refreshToken, _ := helper.GenerateAllTokens(user.Email, user.First_name, user.Last_Name, user.User_type, user.User_id)

		user.Token = token
		user.Refresh_token = refreshToken
		database.ListOfAllUsers = append(database.ListOfAllUsers, user)
		fmt.Println(user)
		fmt.Println(database.ListOfAllUsers)
		c.JSON(http.StatusOK, user)
	}

}


func Login() gin.HandlerFunc {
	return func(c *gin.Context) {
		var user models.User
		var foundUser models.User

		err := c.BindJSON(&user)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}
		for _, userInDB := range database.ListOfAllUsers {
			if userInDB.Email == user.Email {
				foundUser = userInDB
				break
			}
		}
		passwordIsValid, msg := VarifyPassword(user.Password, foundUser.Password)
		if !passwordIsValid {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": msg,
			})
			return
		}

		if foundUser.Email == "" {
			c.JSON(http.StatusInternalServerError, gin.H{
				"error": "user not found",
			})
		}

		token, refreshToken, _ := helper.GenerateAllTokens(foundUser.Email, foundUser.First_name, foundUser.Last_Name, foundUser.User_type, foundUser.User_id)
		helper.UpdateAllTokens(token, refreshToken, foundUser.User_id)

		c.JSON(http.StatusOK, foundUser)

	}

}


func GetUsers() gin.HandlerFunc {
	return func(c *gin.Context) {
		//err := helper.CheckUserType(c, "ADMIN")
		//if err != nil {
		//	c.JSON(http.StatusBadRequest, gin.H{
		//		"error": err.Error(),
		//	})
		//	return
		//}
		c.JSON(http.StatusOK, database.ListOfAllUsers)
		
	}
}



func GetUser() gin.HandlerFunc {

	return func(c *gin.Context) {
		userId := c.Param("user_id")
		err := helper.MatchUserTypeToUid(c, userId)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}
		var user models.User
		for _, userInDb := range database.ListOfAllUsers {
			if userInDb.User_id == userId {
				user = userInDb
				break
			}
		}
		c.JSON(http.StatusOK, user)
	}
}

