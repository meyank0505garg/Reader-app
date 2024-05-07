package routes

import (
	controller "example.com/m/controllers"
	"example.com/m/middleware"
	"github.com/gin-gonic/gin"
)

func UserRoutes(incommingRoutes *gin.Engine) {
	incommingRoutes.Use(middleware.Authenticate())
	incommingRoutes.GET("/users", controller.GetUsers())
	incommingRoutes.GET("/users/:user_id", controller.GetUser())
}
