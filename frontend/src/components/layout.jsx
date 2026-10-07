import SideBar from './SideBar'
import TopBar from './TopBar'



export default function Layout({children}) {
    return(
        <div className="flex h-screen">
            <SideBar/>
            <div className="flex flex-col flex-1">
                <TopBar/>
                <main className="flex-1 overflow-auto p-6 bg-zinc-50">
                    {children}
                </main>
            </div>
        </div>
    )
}