function ShowDockerMenu {
    Clear-Host
    Write-Host "=== Docker Compose Microservices Manager ==="
    Write-Host "1. Build all services"
    Write-Host "2. Start all services (docker-compose up)"
    Write-Host "3. Stop all services (docker-compose down)"
    Write-Host "4. Restart all services"
    Write-Host "5. List all containers"
    Write-Host "6. View container logs"
    Write-Host "7. Exec into a container"
    Write-Host "8. Scale a service"
    Write-Host "9. Stop a single service"
    Write-Host "10. Restart a single service"
    Write-Host "11. View all images"
    Write-Host "12. Remove a specific image"
    Write-Host "13. Remove all unused images"
    Write-Host "14. View all volumes"
    Write-Host "15. Remove a specific volume"
    Write-Host "16. Remove all unused volumes"
    Write-Host "17. View all networks"
    Write-Host "18. Inspect a container"
    Write-Host "19. Docker system prune (clean all unused objects)"
    Write-Host "20. Remove all stopped containers"
    Write-Host "21. Exit"
}

function DockerBuild {
    Write-Host "Building Docker Compose services..."
    docker-compose build
}

function DockerUp {
    Write-Host "Starting Docker Compose services..."
    docker-compose up -d
}

function DockerDown {
    Write-Host "Stopping Docker Compose services..."
    docker-compose down
}

function DockerRestart {
    DockerDown
    DockerUp
}

function ListContainers {
    docker ps -a
}

function ViewLogs {
    $container = Read-Host "Enter container name or ID to view logs"
    docker logs -f $container
}

function ExecContainer {
    $container = Read-Host "Enter container name or ID to exec into"
    docker exec -it $container sh
}

function ScaleService {
    $service = Read-Host "Enter service name"
    $replicas = Read-Host "Enter number of replicas"
    docker-compose up -d --scale "$service=$replicas"
}

function StopService {
    $service = Read-Host "Enter service name to stop"
    docker-compose stop $service
}

function RestartService {
    $service = Read-Host "Enter service name to restart"
    docker-compose restart $service
}

function ListImages {
    docker images
}

function RemoveImage {
    $image = Read-Host "Enter image ID or name to remove"
    docker rmi $image
}

function RemoveUnusedImages {
    docker image prune -af
}

function ListVolumes {
    docker volume ls
}

function RemoveVolume {
    $volume = Read-Host "Enter volume name to remove"
    docker volume rm $volume
}

function RemoveUnusedVolumes {
    docker volume prune -f
}

function ListNetworks {
    docker network ls
}

function InspectContainer {
    $container = Read-Host "Enter container name or ID to inspect"
    docker inspect $container
}

function DockerSystemPrune {
    docker system prune -af
}

function RemoveStoppedContainers {
    docker container prune -f
}

do {
    ShowDockerMenu
    $choice = Read-Host "Choose an option (1-21)"
    switch ($choice) {
        1 { DockerBuild }
        2 { DockerUp }
        3 { DockerDown }
        4 { DockerRestart }
        5 { ListContainers }
        6 { ViewLogs }
        7 { ExecContainer }
        8 { ScaleService }
        9 { StopService }
        10 { RestartService }
        11 { ListImages }
        12 { RemoveImage }
        13 { RemoveUnusedImages }
        14 { ListVolumes }
        15 { RemoveVolume }
        16 { RemoveUnusedVolumes }
        17 { ListNetworks }
        18 { InspectContainer }
        19 { DockerSystemPrune }
        20 { RemoveStoppedContainers }
        21 { break }
        default { Write-Host "Invalid choice." -ForegroundColor Red }
    }
    Write-Host "Press Enter to continue..."
    Read-Host
} while ($true)
