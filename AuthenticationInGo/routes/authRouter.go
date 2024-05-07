package routes

import (
	controller "example.com/m/controllers"
	"github.com/gin-gonic/gin"
)

func AuthRoutes(incommingRoutes *gin.Engine) {
	incommingRoutes.POST("/users/signup", controller.Signup())
	incommingRoutes.POST("/users/login", controller.Login())

}
